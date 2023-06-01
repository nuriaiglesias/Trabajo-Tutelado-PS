package es.udc.cookbook.Users;

import java.util.List;

import es.udc.cookbook.Recipes.Recipe;

public class User {
    public String mail;
    public String name;
    public String password;
    public List<Recipe> favRecipes;

    public User(String mail, String name, String password, List<Recipe> favRecipes) {
        this.mail = mail;
        this.name = name;
        this.password = password;
        this.favRecipes = favRecipes;
    }
}
