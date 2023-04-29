package es.udc.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class UserRecipes extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_recipes);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mis_recetas);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.explorar_recetas:
                        Intent intentExplorar = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intentExplorar);
                    case R.id.recetas_guardadas:
                        // Abrir pantalla de visualización de recetas guardadas por el usuario
                        Intent intentRecetasGuardadas = new Intent(getApplicationContext(), LikedRecipes.class);
                        startActivity(intentRecetasGuardadas);
                        return true;
                }
                return false;
            }
        });
    }


}
