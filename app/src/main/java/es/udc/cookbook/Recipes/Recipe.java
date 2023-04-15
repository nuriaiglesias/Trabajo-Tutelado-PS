package es.udc.cookbook.Recipes;

public class Recipe {
    private final String imageUrl;
    private boolean imageLoaded;
    public Recipe(String imageUrl) {
        this.imageUrl = imageUrl;
        this.imageLoaded = false;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public boolean isImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(boolean imageLoaded) {
        this.imageLoaded = imageLoaded;
    }
}
