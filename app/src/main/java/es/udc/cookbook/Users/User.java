package es.udc.cookbook.Users;
import java.util.List;

public class User {
    public String mail;
    public String name;
    public String password;
    public List<String> favRecipes;

    public User(String mail, String name, String password, List<String> favRecipes) {
        this.mail = mail;
        this.name = name;
        this.password = password;
        this.favRecipes = favRecipes;
    }

    public User() {

    }

    public List<String> getFavRecipes() {
        return favRecipes;
    }

    public void setFavRecipes(List<String> favRecipes) {
        this.favRecipes = favRecipes;
    }
}
