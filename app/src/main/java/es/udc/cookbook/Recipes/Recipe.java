

package es.udc.cookbook.Recipes;

import android.net.Uri;

public class Recipe {
    public String ingredients, imageName, instructions, title, field;
    public Uri uriRecipe = null;
    Boolean imageLoaded = false;

    public Recipe(String ingredients, String imageName, String instructions, String title) {
        this.ingredients = ingredients;
        this.imageName = imageName;
        this.instructions = instructions;
        this.title = title;
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

    public String getField() {
        return field;
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

}