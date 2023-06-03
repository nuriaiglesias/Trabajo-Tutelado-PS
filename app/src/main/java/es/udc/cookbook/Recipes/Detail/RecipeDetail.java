package es.udc.cookbook.Recipes.Detail;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashSet;
import java.util.Set;

import es.udc.cookbook.R;
import es.udc.cookbook.Recipes.Constructor.Recipe;
import es.udc.cookbook.Recipes.FavRecipes;

public class RecipeDetail extends AppCompatActivity {
    DatabaseReference ref;
    SharedPreferences preferences;
    String username;
    String user;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);

        TextView creator = findViewById(R.id.creatorRecipe);

        TextView titleDt = findViewById(R.id.TitleDetail);
        TextView recipeInfo = findViewById(R.id.RecipeInfo);
        ImageView imageDt = findViewById(R.id.ImageDetail);

        //Recuperamos la información de la receta
        user = getIntent().getStringExtra("user");
        String recipeId = getIntent().getStringExtra("id");

        //Botón para dar "like"
        ImageButton likeButton = findViewById(R.id.likeButton);
        //Recuperamos referencia a bd
        ref = FirebaseDatabase.getInstance().getReference();
        //Recuperamos info del usuario
        preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        username = preferences.getString("username", "");

        // Obtenemos la info de la recta a través del ID
        Recipe.getRecipeById(recipeId, new Recipe.RecipeCallback() {
            @Override
            public void onRecipeLoaded(Recipe recipe) {
                //Mostramos la imagen
                showImage(imageDt, recipe.imageName);
                //Mostramos el Título
                titleDt.setText(recipe.title);
                creator.setText(user);
                //Mostramos ingredientes
                String result = changeFomat(recipe.ingredients);
                recipeInfo.setText(result);
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewRecipies);
                bottomNavigationView.setSelectedItemId(R.id.ingredientes);
                bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ingredientes:
                                recipeInfo.setText(result);
                                return true;
                            case R.id.detalles:
                                recipeInfo.setText(recipe.instructions);
                                return true;
                        }
                        return false;
                    }
                });

            }

            @Override
            public void onError(DatabaseError databaseError) {
                Toast.makeText(RecipeDetail.this, "Error obtaining recipe", Toast.LENGTH_SHORT).show();
            }
        });

        // Inicializar el estado del botón de "me gusta" en función de las preferencias
        FavRecipes.initializeLikeButtonState(recipeId, likeButton, preferences);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavRecipes.handleFavoriteRecipe(recipeId, likeButton, preferences);
            }
        });

        //Añadimos botón para volver a la página anterior
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar2);
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showImage(ImageView imageView, String uriR){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages");
        storageRef.child(uriR).getDownloadUrl().addOnSuccessListener(uri -> {
            if (uri != null) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(e -> Log.d("RecipeAdapter", "Failed to load image"));
    }

    public void followCreator(View view) {
        preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        String loggedInUser = preferences.getString("username", ""); // Obtén el nombre de usuario del usuario conectado
        Set<String> followedUsers = preferences.getStringSet(loggedInUser + "_followed_users", new HashSet<>()); // Obtén el conjunto de usuarios seguidos del usuario conectado
        Set<String> updatedFollowedUsers = new HashSet<>(followedUsers);
        updatedFollowedUsers.add(user);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(loggedInUser + "_followed_users", updatedFollowedUsers); // Asocia la lista de seguidos al usuario conectado
        editor.apply();

        String followUser = getString(R.string.followUser);
        // Muestra un mensaje de éxito
        Toast.makeText(this, followUser + user, Toast.LENGTH_SHORT).show();
    }

    private String changeFomat(String ingredients) {
        StringBuilder output = new StringBuilder();
        if (ingredients.startsWith("[")) {
            ingredients = ingredients.substring(1, ingredients.length() - 1);
            String[] elements = ingredients.split(", ");
            for (String element : elements) {
                element = element.substring(1, element.length() - 1);
                output.append("- ").append(element).append("\n");
            }
        } else {
            output.append("- ").append(ingredients).append("\n");
        }
        return output.toString();
    }


}