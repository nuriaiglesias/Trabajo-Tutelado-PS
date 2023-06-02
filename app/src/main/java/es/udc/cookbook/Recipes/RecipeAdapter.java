package es.udc.cookbook.Recipes;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.udc.cookbook.R;
import es.udc.cookbook.Users.User;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> implements Filterable {
    private final ArrayList<Recipe> recipesList;
    private final ArrayList<Recipe> recipeListFull;
    private final SharedPreferences sharedPreferences;
    private final Context mContext;


    public RecipeAdapter(Context mContext, ArrayList<Recipe> mDataset, SharedPreferences sharedPreferences) {
        this.mContext = mContext;
        this.recipesList = mDataset;
        recipeListFull = new ArrayList<>(recipesList);
        this.sharedPreferences = sharedPreferences;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public ImageView image;
        public  ImageButton fav;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.txtTitle);
            image = view.findViewById(R.id.imageViewRecipe);
            fav = view.findViewById(R.id.likeButtonlist);
            view.setOnClickListener(this);
        }

        @Override
        public  void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    // Definimos interfaz
    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }
    // Definimos variable y función para guardar el listener
    private static OnItemClickListener clickListener;
    public void setClickListener(OnItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }



    @NonNull
    @Override
    public RecipeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_tile, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages");
        Recipe recipe = recipesList.get(position);
        holder.title.setText(recipesList.get(position).getTitle());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //Recuperamos info del usuario

        String username = sharedPreferences.getString("username", "");

        boolean isLiked = sharedPreferences.getBoolean(recipe.id, false); // Obtener el estado actualizado desde las preferencias
        holder.fav.setImageResource(isLiked ? R.drawable.ic_active_like : R.drawable.ic_inactive_like); // Establecer el recurso del botón según el estado

        // Obtener una referencia al usuario
        DatabaseReference userRef = ref.child("Usuarios").child(username);
        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavRecipes.handleFavoriteRecipe(recipe.id, holder.fav, sharedPreferences);
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
            }).addOnFailureListener(e -> Log.d("RecipeAdapter", "Failed to load image for position " + position));
        } else if (recipe.getUriRecipe() != null) {
            Glide.with(mContext)
                    .load(recipe.uriRecipeLoad)
                    .into(holder.image);
        } else {
            Log.d("RecipeAdapter", "No image found for position " + position);
        }
    }


    @Override
    public int getItemCount() {
        return recipesList.size();
    }


    @Override
    public Filter getFilter() {
        return recipeFilter;
    }

    private final Filter recipeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recipe> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(recipeListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Recipe recipe : recipeListFull) {
                    if (recipe.getTitle().toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(recipe);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recipesList.clear();
            recipesList.addAll((Collection<? extends Recipe>) results.values);
            notifyDataSetChanged();
        }
    };

}
