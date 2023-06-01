package es.udc.cookbook.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import es.udc.cookbook.R;
import es.udc.cookbook.Recipes.LikedRecipes;
import es.udc.cookbook.Recipes.Recipe;
import es.udc.cookbook.Recipes.RecipeAdapter;
import es.udc.cookbook.Recipes.RecipeDetail;
import es.udc.cookbook.Recipes.UserRecipes;


public class MainActivity extends AppCompatActivity {
    public ArrayList<Recipe> recipes = new ArrayList<>();
    RecyclerView recyclerView;
    //Firebase
    private DatabaseReference ref;
    private RecipeAdapter recipeAdapter;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Firebase
        ref = FirebaseDatabase.getInstance().getReference();

        // Barra de búsqueda
        searchView = findViewById(R.id.searchView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.explorar_recetas);
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
                        return true;
                    case R.id.recetas_guardadas:
                        // Abrir pantalla de visualización de recetas guardadas por el usuario
                        Intent intentRecetasGuardadas = new Intent(getApplicationContext(), LikedRecipes.class);
                        startActivity(intentRecetasGuardadas);
                        return true;
                }
                return false;
            }
        });

        GetDataFromFirebase();
    }
    private void GetDataFromFirebase() {
        Query query = ref.child("Recetas");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    Recipe recipe = new Recipe();
                    recipe.setImageName(Objects.requireNonNull(snapshot.child("imageName").getValue()).toString());
                    recipe.setTitle(Objects.requireNonNull(snapshot.child("title").getValue()).toString());
                    recipe.setInstructions(Objects.requireNonNull(snapshot.child("instructions").getValue()).toString());
                    recipe.setCleanedIngredients(Objects.requireNonNull(snapshot.child("cleanedIngredients").getValue()).toString());
                    recipe.setUser(Objects.requireNonNull(snapshot.child("user").getValue()).toString());
                    recipes.add(recipe);
                }
                recipeAdapter = new RecipeAdapter(getApplicationContext(),recipes);
                recyclerView.setAdapter(recipeAdapter);

                recipeAdapter.setClickListener(new RecipeAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, RecipeDetail.class);
                        intent.putExtra("title", recipes.get(position).title);
                        intent.putExtra("instructions", recipes.get(position).instructions);
                        intent.putExtra("ingredients", recipes.get(position).ingredients);
                        intent.putExtra("image", recipes.get(position).uriRecipe.toString());
                        intent.putExtra("user", recipes.get(position).user);
                        startActivity(intent);
                    }
                });
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(String s) {
                        recipeAdapter.getFilter().filter(s);
                        return true;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
