package es.udc.cookbook.Recipes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import es.udc.cookbook.R;
import es.udc.cookbook.Users.User;

public class RecipeAdapter2 extends RecyclerView.Adapter<RecipeAdapter2.MyViewHolder> {
    private final ArrayList<Recipe> recipesList;
    private final Context mContext;
    SharedPreferences sharedPreferences;

    public RecipeAdapter2(Context mContext, ArrayList<Recipe> mDataset, SharedPreferences sharedPreferences) {
        this.mContext = mContext;
        this.recipesList = mDataset;
        this.sharedPreferences = sharedPreferences;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public ImageView image;
        public ImageButton fav;

        public ImageButton deleteButton;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.txtTitle);
            image = view.findViewById(R.id.imageViewRecipe);
            fav = view.findViewById(R.id.likeButtonlist);
            deleteButton = view.findViewById(R.id.deleteButton);
            view.setOnClickListener(this);
        }

        @Override
        public  void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    // Definimos interfaz
    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
    // Definimos variable y función para guardar el listener
    private static OnItemClickListener clickListener;
    public void setClickListener(OnItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecipeAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recipes_grid, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages");
        Recipe recipe = recipesList.get(position);
        holder.title.setText(recipesList.get(position).getTitle());

        //Recuperamos info del usuario
        boolean isLiked = sharedPreferences.getBoolean(recipe.id, false); // Obtener el estado actualizado desde las preferencias
        holder.fav.setImageResource(isLiked ? R.drawable.ic_active_like : R.drawable.ic_inactive_like); // Establecer el recurso del botón según el estado

        // Inicializar el estado del botón de "me gusta" en función de las preferencias
        FavRecipes.initializeLikeButtonState(recipe.id, holder.fav, sharedPreferences);

        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavRecipes.handleFavoriteRecipe(recipe.id, holder.fav, sharedPreferences);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecipe(recipe);
            }
        });

        //ImageView
        if (!recipe.isImageLoaded()) { // cargar la imagen solo si aún no se ha cargado
            storageRef.child(recipe.imageName).getDownloadUrl().addOnSuccessListener(uri -> {
                if (uri != null) {
                    Glide.with(mContext)
                            .load(uri)
                            .into(holder.image);
                    recipe.setImageLoaded(true);
                    recipe.setUriRecipe(uri);
                }
            }).addOnFailureListener(e -> Log.d("RecipeAdapter2", "Failed to load image for position " + position));
        } else if (recipe.getUriRecipe() != null) {
            Glide.with(mContext)
                    .load(recipe.uriRecipeLoad)
                    .into(holder.image);
        } else {
            Log.d("RecipeAdapter2", "No image found for position " + position);
        }
    }

    private void deleteRecipe(Recipe recipe) {
        // Eliminar la receta de la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("Recetas").child(recipe.id).removeValue();

        // Eliminar la imagen asociada en Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("FoodImages").child(recipe.imageName);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // La imagen se eliminó exitosamente
                Log.d("RecipeAdapter2", "Image deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Ocurrió un error al eliminar la imagen
                Log.d("RecipeAdapter2", "Failed to delete image: " + e.getMessage());
            }
        });

        // Eliminar la receta de la lista de favoritos de todos los usuarios
        DatabaseReference usersRef = databaseRef.child("Usuarios");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && user.getFavRecipes() != null) {
                        List<String> favRecipes = user.getFavRecipes();
                        if (favRecipes.contains(recipe.id)) {
                            favRecipes.remove(recipe.id);
                            usersRef.child(userSnapshot.getKey()).child("favRecipes").setValue(favRecipes);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Ocurrió un error al eliminar la imagen
                Log.d("RecipeAdapter2", "Failed to delete image");
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipesList.size();
    }

}

