package es.udc.cookbook.Recipes.Detail;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class RecipeDetailUser extends AppCompatActivity {
    DatabaseReference ref;
    // Recuperamos nombre usuario actual
    SharedPreferences preferences;
    String username;
    String user;
    EditText titleDt;
    EditText ingredientsEt;
    EditText instructions;
    String recipeId;

    private boolean isEditing = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_user);

        TextView creator = findViewById(R.id.creatorRecipe);

        titleDt = findViewById(R.id.TitleDetail);
        ingredientsEt = findViewById(R.id.ingredientsEditText);
        instructions = findViewById(R.id.instructionsEditText);
        ImageView imageDt = findViewById(R.id.ImageDetail);

        //Recuperamos la información de la receta
        user = getIntent().getStringExtra("user");
        recipeId = getIntent().getStringExtra("id");

        //Botón para dar "like"
        ImageButton likeButton = findViewById(R.id.likeButton);
        //Recuperamos referencia a bd
        ref = FirebaseDatabase.getInstance().getReference();
        //Recuperamos info del usuario
        preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        username = preferences.getString("username", "");

        titleDt.setEnabled(false);
        instructions.setEnabled(false);
        ingredientsEt.setEnabled(false);
        instructions.setVisibility(View.GONE);


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
                ingredientsEt.setText(result);
                instructions.setText(recipe.instructions);
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewRecipies);
                bottomNavigationView.setSelectedItemId(R.id.ingredientes);
                bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ingredientes:
                                instructions.setVisibility(View.GONE);
                                ingredientsEt.setVisibility(View.VISIBLE);
                                return true;
                            case R.id.detalles:
                                instructions.setVisibility(View.VISIBLE);
                                ingredientsEt.setVisibility(View.GONE);
                                return true;
                        }
                        return false;
                    }
                });

            }

            @Override
            public void onError(DatabaseError databaseError) {
                Toast.makeText(RecipeDetailUser.this, "Error obtaining recipe", Toast.LENGTH_SHORT).show();
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


        ImageButton editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing) {
                    // Guardar los cambios y deshabilitar la edición
                    saveChanges();
                    disableEditing();
                } else {
                    // Habilitar la edición
                    enableEditing();
                }
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

    private void enableEditing() {
        isEditing = true;
        titleDt.setEnabled(true);
        instructions.setEnabled(true);
        ingredientsEt.setEnabled(true);
    }

    private void disableEditing() {
        isEditing = false;
        titleDt.setEnabled(false);
        instructions.setEnabled(false);
        ingredientsEt.setEnabled(false);
    }

    private void saveChanges() {
        String newTitle = titleDt.getText().toString();
        String newInstructions = instructions.getText().toString();
        String newResult = ingredientsEt.getText().toString();
        DatabaseReference recipeRef = ref.child("Recetas").child(recipeId);

        // Actualiza los campos de título y receta en la base de datos
        recipeRef.child("title").setValue(newTitle);
        recipeRef.child("instructions").setValue(newInstructions);
        recipeRef.child("ingredients").setValue(newResult);

        Toast.makeText(RecipeDetailUser.this, "Changes saved", Toast.LENGTH_SHORT).show();
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
