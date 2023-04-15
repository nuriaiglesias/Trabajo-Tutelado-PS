package es.udc.cookbook.Recipes;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import es.udc.cookbook.R;
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ImageViewHolder> {
    private final List<Recipe> imageList;

    public RecipeAdapter(List<Recipe> imageList) {
        this.imageList = imageList;
        Log.d("RecipeAdapter", "imageList size: " + imageList.size());
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_tile, parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if (position < imageList.size()) {
            Recipe recipe = imageList.get(position);
            if (!recipe.isImageLoaded()) { // cargar la imagen solo si aún no se ha cargado
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages").child(recipe.getImageUrl());
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (uri != null) {
                        Glide.with(holder.itemView.getContext())
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.imageView);
                        recipe.setImageLoaded(true); // indicar que la imagen se ha cargado
                        notifyDataSetChanged(); // notificar al adaptador que los datos han cambiado
                    }
                }).addOnFailureListener(e -> Log.d("RecipeAdapter", "position " + position + " is out of range for imageList size " + imageList.size()));
            }
        } else {
            Log.d("RecipeAdapter", "position " + position + " is out of range for imageList size " + imageList.size());
        }
    }




    @Override
    public int getItemCount() {
        Log.d("RecipeAdapter", "hola"+ imageList.size());
        return imageList.size();
    }

    public void updateImageList(List<Recipe> imageList) {
        this.imageList.clear();
        this.imageList.addAll(imageList);
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recipe_image_view);
        }
    }
}

