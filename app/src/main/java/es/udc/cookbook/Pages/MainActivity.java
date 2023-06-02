package es.udc.cookbook.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

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
import es.udc.cookbook.Recipes.FavRecipes;
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
    AlertDialog dialog;
    private boolean celiacFilter = false;
    private boolean veganFilter = false;
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
                        Intent intentRecetasGuardadas = new Intent(getApplicationContext(), FavRecipes.class);
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
                    recipe.setUser(Objects.requireNonNull(snapshot.child("user").getValue()).toString());
                    recipe.setIngredients(Objects.requireNonNull(snapshot.child("ingredients").getValue()).toString());
                    recipe.setId(Objects.requireNonNull(snapshot.child("id").getValue()).toString());
                    recipe.setTag(Objects.requireNonNull(snapshot.child("tag").getValue()).toString());
                    recipes.add(recipe);
                }
                SharedPreferences sharedPreferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
                recipeAdapter = new RecipeAdapter(getApplicationContext(),recipes, sharedPreferences);
                recyclerView.setAdapter(recipeAdapter);

                recipeAdapter.setClickListener(new RecipeAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, RecipeDetail.class);
                        intent.putExtra("user", recipes.get(position).user);
                        intent.putExtra("id", recipes.get(position).id);
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

    public void filters(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select filters");
        // Configura los elementos de la lista y su estado de selección
        String[] filters = {"Celiac", "Vegan"};
        boolean[] checkedItems = {false, false};
        builder.setMultiChoiceItems(filters, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Actualiza el estado de selección al hacer clic en un elemento de la lista
                if (which == 0) {
                    celiacFilter = isChecked;
                } else if (which == 1) {
                    veganFilter = isChecked;
                }
            }
        });
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aplica los filtros seleccionados
                celiacFilter = checkedItems[0];
                veganFilter = checkedItems[1];

                recipeAdapter.setCeliacFilter(celiacFilter);
                recipeAdapter.setVeganFilter(veganFilter);
                recipeAdapter.filterData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();

        // Marcar los elementos seleccionados según el estado de los filtros
        ListView listView = dialog.getListView();
        listView.setItemChecked(0, celiacFilter);
        listView.setItemChecked(1, veganFilter);
    }


}
