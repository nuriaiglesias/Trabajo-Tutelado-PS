

package es.udc.cookbook.Recipes;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Recipe {
    public String ingredients, imageName, instructions, title, id, user;
    public Uri uriRecipe = null;
    Boolean imageLoaded = false;

    public Recipe(String ingredients, String imageName, String instructions, String title, String id, String user) {
        this.ingredients = ingredients;
        this.imageName = imageName;
        this.instructions = instructions;
        this.title = title;
        this.id = id;
        this.user = user;
    }

    public Recipe(String title, String imageName, Uri uriRecipe) {
        this.imageName = imageName;
        this.title = title;
        this.uriRecipe = uriRecipe;
    }

    public Recipe() {

    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String Image_Name) {
        this.imageName = Image_Name + ".jpg";
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String ins) {
        this.instructions = ins;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String Title) {
        this.title = Title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser(String User) {
        this.user = User;
    }

    public String getUser() {
        return user;
    }

    public boolean isImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(boolean ImageLoaded) {
        this.imageLoaded = ImageLoaded;

    }

    public boolean isUriRecipe() {
        return uriRecipe != null;
    }
    public void setUriRecipe(Uri uriRecipe) {
        this.uriRecipe = uriRecipe;

    }
    public Uri getUriRecipe() {
        return uriRecipe;
    }

    public interface RecipeCallback {
        void onRecipeLoaded(Recipe recipe);
        void onError(DatabaseError databaseError);
    }

    public static void getRecipeById(String recipeId, RecipeCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        DatabaseReference recipeRef = ref.child("Recetas").child(recipeId);

        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ingredients = dataSnapshot.child("ingredients").getValue(String.class);
                    String imageName = dataSnapshot.child("imageName").getValue(String.class);
                    String instructions = dataSnapshot.child("instructions").getValue(String.class);
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String user = dataSnapshot.child("user").getValue(String.class);

                    Recipe recipe = new Recipe();
                    recipe.setIngredients(ingredients);
                    recipe.setImageName(imageName);
                    recipe.setInstructions(instructions);
                    recipe.setTitle(title);
                    recipe.setUser(user);
                    recipe.setId(recipeId);


                    callback.onRecipeLoaded(recipe);
                } else {
                    callback.onError(null); // o pasa un objeto de error personalizado si lo deseas
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError);
            }
        });
    }



}