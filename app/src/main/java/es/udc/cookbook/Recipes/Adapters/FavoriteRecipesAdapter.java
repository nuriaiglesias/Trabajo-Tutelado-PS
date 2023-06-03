package es.udc.cookbook.Recipes.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;
import es.udc.cookbook.R;
import es.udc.cookbook.Recipes.Constructor.Recipe;


public class FavoriteRecipesAdapter extends RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder> {
    private final List<String> recipesList;
    private final Context mContext;


    public FavoriteRecipesAdapter(Context mContext, List<String> recipesList) {
        this.recipesList = recipesList;
        this.mContext = mContext;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.txtTitle);
            image = view.findViewById(R.id.imageViewRecipe);
        }

    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public FavoriteRecipesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String recipeId = recipesList.get(position);

        Recipe.getRecipeById(recipeId, new Recipe.RecipeCallback() {
            @Override
            public void onRecipeLoaded(Recipe recipe) {
                holder.title.setText(recipe.getTitle());
                showImage(holder.image, recipe.imageName);
            }

            @Override
            public void onError(DatabaseError databaseError) {
                Toast.makeText(mContext, "Error load recipe", Toast.LENGTH_SHORT).show();
            }
        });
        final int itemPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(itemPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipesList.size();
    }

    private void showImage(ImageView imageView, String uriR){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages");
        storageRef.child(uriR).getDownloadUrl().addOnSuccessListener(uri -> {
            if (uri != null) {
                Glide.with(mContext.getApplicationContext())
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(e -> Log.d("RecipeAdapter", "Failed to load image"));
    }
}
