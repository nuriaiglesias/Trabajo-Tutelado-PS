package es.udc.cookbook.Recipes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.udc.cookbook.R;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {
    private final ArrayList<Recipe> mDataset;

    public RecipeAdapter(ArrayList<Recipe> mDataset) {
        this.mDataset = mDataset;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView ingredients, image, instructions, title;
        public MyViewHolder(View view) {
            super(view);
            ingredients = view.findViewById(R.id.recipe_Name_Tile);
            image = view.findViewById(R.id.recipe_Description_Tile);
            instructions = view.findViewById(R.id.recipe_Description_Tile);
            title = view.findViewById(R.id.recipe_Description_Tile);
            view.setOnClickListener(this);
        }
        public void bind(Recipe article) {
            ingredients.setText(article.getIngredients());
            image.setText(article.getImage());
            instructions.setText(article.getInstructions());
            title.setText(article.getTitle());

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
    public RecipeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_tile, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.MyViewHolder holder, int position) {
        holder.bind(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
