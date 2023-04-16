package es.udc.cookbook.Recipes;

import java.util.List;

public class Recipe {
    public String ingredients, image, instructions, title, field;
    Boolean imageLoaded = false;
    String imageUrl;

    public Recipe(String ingredients, String image, String instructions, String title, String field) {
        this.ingredients = ingredients;
        this.image = image;
        this.instructions = instructions;
        this.title = title;
        this.field = field;
    }

    public Recipe(String title, String image) {
        this.image = image;
        this.title = title;
    }

    public Recipe() {

    }


    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image + ".jpg";
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getField() {
        return field;
    }


    public boolean isImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(boolean imageLoaded) {
        this.imageLoaded = imageLoaded;
    }
}
