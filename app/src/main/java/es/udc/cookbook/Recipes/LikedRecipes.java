package es.udc.cookbook.Recipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import es.udc.cookbook.Pages.MainActivity;
import es.udc.cookbook.R;

public class LikedRecipes extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_recipes);

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
}
