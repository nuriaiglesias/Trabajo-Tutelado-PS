package es.udc.cookbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class UserRecipes extends AppCompatActivity {

    private Button createRecipeButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_recipes);

        createRecipeButton = findViewById(R.id.createRecipeButton);
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

        // Para mostrar el nombre de usuario
        SharedPreferences preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        String username = preferences.getString("username", "");
        if (!username.isEmpty()) {
            TextView usernameTextView = findViewById(R.id.user_name);
            usernameTextView.setText(username);
        } else {
            Toast.makeText(getApplicationContext(),"No detectado el nombre correctamente", Toast.LENGTH_LONG).show();
        }

        // Asignar la acción al botón
        assignCreateRecipeAction();
    }
    private void assignCreateRecipeAction() {
        createRecipeButton.setOnClickListener(v -> {
            Intent intentAddRecipe = new Intent(getApplicationContext(), AddRecipe.class);
            startActivity(intentAddRecipe);
        });
    }

}
