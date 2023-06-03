package es.udc.cookbook.Recipes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.udc.cookbook.Pages.MainActivity;
import es.udc.cookbook.R;
import es.udc.cookbook.Users.User;

public class FavRecipes extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_recipes);
        // Recuperamos nombre usuario actual
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        SharedPreferences preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        String username = preferences.getString("username", "");

        DatabaseReference userRef = ref.child("Usuarios").child(username);
        DatabaseReference favRecipesRef = userRef.child("favRecipes");

        if (!username.isEmpty()) {
            TextView usernameTextView = findViewById(R.id.user_nameF);
            usernameTextView.setText(username);
        } else {
            Toast.makeText(getApplicationContext(),"Not detected the username", Toast.LENGTH_LONG).show();
        }
        //Inicializamos el grid con las recetas favoritas de cada usuario
        favRecipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> favRecipes = new ArrayList<>();
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        String recipeIdFav = recipeSnapshot.getValue(String.class);
                        favRecipes.add(recipeIdFav);
                    }
                    //Pagina dónde se muestran las recetas favoritas
                    RecyclerView recyclerView = findViewById(R.id.recycleViewFav);
                    GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
                    recyclerView.setLayoutManager(layoutManager);
                    List<String> loadedRecipes = new ArrayList<>();

                    for (String recipeId : favRecipes) {
                        Recipe.getRecipeById(recipeId, new Recipe.RecipeCallback() {
                            @Override
                            public void onRecipeLoaded(Recipe recipe) {
                                loadedRecipes.add(recipeId);
                                if (loadedRecipes.size() == favRecipes.size()) {
                                    // Se han cargado todas las recetas, crear el adaptador y establecerlo en el RecyclerView
                                    FavoriteRecipesAdapter adapter = new FavoriteRecipesAdapter(FavRecipes.this, loadedRecipes);
                                    recyclerView.setAdapter(adapter);
                                    adapter.setOnItemClickListener(new FavoriteRecipesAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            String clickedRecipeId = loadedRecipes.get(position);
                                            Recipe.getRecipeById(clickedRecipeId, new Recipe.RecipeCallback() {
                                                @Override
                                                public void onRecipeLoaded(Recipe recipe) {
                                                    String user = recipe.getUser();
                                                    Intent intent = new Intent(FavRecipes.this, RecipeDetail.class);
                                                    intent.putExtra("id", clickedRecipeId);
                                                    intent.putExtra("user", user);
                                                    startActivity(intent);
                                                }

                                                @Override
                                                public void onError(DatabaseError databaseError) {
                                                    Toast.makeText(FavRecipes.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(DatabaseError databaseError) {
                                Toast.makeText(FavRecipes.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    // No se encontraron favoritos para el usuario actual
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FavRecipes.this,"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.recetas_guardadas);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mis_recetas:
                        // Abrir pantalla de visualización de recetas creadas por el usuario
                        Intent intentMisRecetas = new Intent(getApplicationContext(), UserRecipes.class);
                        startActivity(intentMisRecetas);
                        return true;
                    case R.id.explorar_recetas:
                        Intent intentExplorar = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intentExplorar);
                        return true;
                    case R.id.recetas_guardadas:
                        // Abrir pantalla de visualización de recetas guardadas por el usuario
                        return true;
                }
                return false;
            }
        });


    }



    public static void handleFavoriteRecipe(String recipeId, ImageView likeButton, SharedPreferences preferences) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        // Recuperamos nombre usuario actual

        String username = preferences.getString("username", "");
        DatabaseReference userRef = ref.child("Usuarios").child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        List<String> favRecipes = user.getFavRecipes();
                        if (favRecipes != null && favRecipes.contains(recipeId)) {
                            removeRecipeFromFavorites(userRef, user, recipeId);
                            preferences.edit().putBoolean(recipeId, false).apply(); // Guardar estado "no me gusta" en las preferencias
                            likeButton.setImageResource(R.drawable.ic_inactive_like);
                        } else {
                            addRecipeToFavorites(userRef, user, recipeId);
                            preferences.edit().putBoolean(recipeId, true).apply(); // Guardar estado "me gusta" en las preferencias
                            likeButton.setImageResource(R.drawable.ic_active_like);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error aquí si es necesario
            }
        });
    }

    public static void addRecipeToFavorites(DatabaseReference userRef, User user, String recipeId) {
        List<String> favRecipes = user.getFavRecipes();
        if (favRecipes == null) {
            favRecipes = new ArrayList<>();
        }
        favRecipes.add(recipeId); // Añadir el ID de la receta a la lista
        user.setFavRecipes(favRecipes);
        userRef.setValue(user);
    }

    public static void removeRecipeFromFavorites(DatabaseReference userRef, User user, String recipeId) {
        List<String> favRecipes = user.getFavRecipes();
        if (favRecipes != null) {
            favRecipes.remove(recipeId); // Eliminar el ID de la receta de la lista
            user.setFavRecipes(favRecipes);
            userRef.setValue(user);
        }
    }

    public static void initializeLikeButtonState(String recipeId, ImageView likeButton, SharedPreferences preferences) {
        boolean isLiked = preferences.getBoolean(recipeId, false); // Obtener el estado actualizado desde las preferencias
        likeButton.setImageResource(isLiked ? R.drawable.ic_active_like : R.drawable.ic_inactive_like); // Establecer el recurso del botón según el estado
    }

}