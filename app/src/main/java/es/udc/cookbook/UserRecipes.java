package es.udc.cookbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import es.udc.cookbook.Recipes.Recipe;
import es.udc.cookbook.Recipes.RecipeAdapter;
import es.udc.cookbook.Recipes.RecipeDetail;

public class UserRecipes extends AppCompatActivity {
    DatabaseReference ref = null;
    private RecipeAdapter2 recipeAdapter;
    int count = 0;
    private ArrayList<Recipe> recipes;
    RecyclerView recyclerView;
    // Para mostrar el nombre de usuario
    SharedPreferences preferences;
    String username;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_recipes);
        recyclerView = findViewById(R.id.recycleView2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        ref = FirebaseDatabase.getInstance().getReference();
        recipes = new ArrayList<>();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mis_recetas);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.explorar_recetas:
                        Intent intentExplorar = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intentExplorar);
                        return true;
                    case R.id.recetas_guardadas:
                        // Abrir pantalla de visualización de recetas guardadas por el usuario
                        Intent intentRecetasGuardadas = new Intent(getApplicationContext(), LikedRecipes.class);
                        startActivity(intentRecetasGuardadas);
                        return true;
                    case R.id.mis_recetas:
                        // Abrir pantalla de visualización de recetas guardadas por el usuario
                        Toast.makeText(getApplicationContext(), "Ya estás en la pantalla de tus recetas", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        username = preferences.getString("username", "");

        if (!username.isEmpty()) {
            TextView usernameTextView = findViewById(R.id.user_name);
            usernameTextView.setText(username);
        } else {
            Toast.makeText(getApplicationContext(),"No detectado el nombre correctamente", Toast.LENGTH_LONG).show();
        }

        GetDataFromFirebase();

    }
    private void GetDataFromFirebase() {
        Query query = ref.child("Recetas").orderByChild("user").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                recipes.clear();

                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    count++;
                    Recipe recipe = new Recipe();
                    recipe.setImageName(Objects.requireNonNull(snapshot.child("imageName").getValue()).toString());
                    recipe.setTitle(Objects.requireNonNull(snapshot.child("title").getValue()).toString());
                    recipe.setInstructions(Objects.requireNonNull(snapshot.child("instructions").getValue()).toString());
                    recipe.setCleanedIngredients(Objects.requireNonNull(snapshot.child("cleanedIngredients").getValue()).toString());
                    recipes.add(recipe);
                }
                recipeAdapter = new RecipeAdapter2(getApplicationContext(), recipes);
                recyclerView.setAdapter(recipeAdapter);

                recipeAdapter.setClickListener(new RecipeAdapter2.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Log.d("_TAG", " Item " + recipes.get(position).imageName);
                        Intent intent = new Intent(UserRecipes.this, RecipeDetail.class);
                        intent.putExtra("title", recipes.get(position).title);
                        intent.putExtra("image", recipes.get(position).imageName);
                        intent.putExtra("instructions", recipes.get(position).instructions);
                        intent.putExtra("ingredients", recipes.get(position).ingredients);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void createRecipe(View view) {
        Intent intentAddRecipe = new Intent(getApplicationContext(), AddRecipe.class);
        startActivity(intentAddRecipe);
    }
    public void settings(View view) {
        Intent intentSettings = new Intent(getApplicationContext(), Settings.class);
        startActivity(intentSettings);
    }

}
