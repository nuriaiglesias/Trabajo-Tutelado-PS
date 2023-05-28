package es.udc.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.Objects;

import es.udc.cookbook.Recipes.Recipe;
import es.udc.cookbook.Recipes.RecipeAdapter;
import es.udc.cookbook.Recipes.RecipeDetail;


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
                        Toast.makeText(getApplicationContext(), "Ya estás en la pantalla de explorar", Toast.LENGTH_SHORT).show();
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
                    recipe.setIngredients(Objects.requireNonNull(snapshot.child("cleanedIngredients").getValue()).toString());
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
                        if (s.isEmpty()) {
                            GetDataFromFirebase();
                        } else {
                            search(s);
                        }
                        return true;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void filterList(ArrayList<Recipe> filteredList) {
        recipes.clear();
        recipes.addAll(filteredList);
        //recipeAdapter.notifyDataSetChanged();
    }
    private void search(String searchText) {
        ArrayList<Recipe> filteredList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.getTitle().toLowerCase(Locale.getDefault()).contains(searchText.toLowerCase(Locale.getDefault()))) {
                filteredList.add(recipe);
            }
        }
        filterList(filteredList);
    }

}
