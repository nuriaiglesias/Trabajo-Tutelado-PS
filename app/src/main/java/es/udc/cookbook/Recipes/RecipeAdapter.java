package es.udc.cookbook.Recipes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import es.udc.cookbook.R;
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {
    private final ArrayList<Recipe> recipesList;
    private Context mContext;

    public RecipeAdapter(Context mContext, ArrayList<Recipe> mDataset) {
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
    public RecipeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_tile, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages");
        holder.title.setText(recipesList.get(position).getTitle());
        //ImageView
        System.out.println("posicion " +position + "tamaño lista" + recipesList.size());
        //esto es provisional
        if (position < 21) {
            Recipe recipe = recipesList.get(position);
            if (!recipe.isImageLoaded()) { // cargar la imagen solo si aún no se ha cargado
                storageRef.child(recipe.getImage()).getDownloadUrl().addOnSuccessListener(uri -> {
                    if (uri != null) {
                        Glide.with(mContext)
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.image);
                        recipe.setImageLoaded(true); // indicar que la imagen se ha cargado
                        notifyDataSetChanged(); // notificar al adaptador que los datos han cambiado
                    }
                }).addOnFailureListener(e -> Log.d("RecipeAdapter", "position " + position + " is out of range for imageList size " + recipesList.size()));
            }
        } else {
            Log.d("RecipeAdapter", "position " + position + " is out of range for imageList size " + recipesList.size());
        }
    }
        /*StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages");

        Glide.with(mContext)
                .load(recipesList.get(position).getImage())
               .into(holder.image);

         */

    @Override
    public int getItemCount() {
        return recipesList.size();
    }

}


