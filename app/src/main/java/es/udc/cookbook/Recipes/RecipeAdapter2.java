package es.udc.cookbook.Recipes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.udc.cookbook.R;
import es.udc.cookbook.Recipes.Recipe;

public class RecipeAdapter2 extends RecyclerView.Adapter<RecipeAdapter2.MyViewHolder> {
    private final ArrayList<Recipe> recipesList;
    private final Context mContext;

    public RecipeAdapter2(Context mContext, ArrayList<Recipe> mDataset) {
        this.mContext = mContext;
        this.recipesList = mDataset;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.txtTitle);
            image = view.findViewById(R.id.imageViewRecipe);
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
    public RecipeAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_tile, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages");
        Recipe recipe = recipesList.get(position);
        holder.title.setText(recipesList.get(position).getTitle());
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
        } else if (recipe.isUriRecipe()) {
            Glide.with(mContext)
                    .load(recipe.uriRecipe)
                    .into(holder.image);
        } else {
            Log.d("RecipeAdapter2", "No image found for position " + position);
        }
    }


    @Override
    public int getItemCount() {
        return recipesList.size();
    }

}

