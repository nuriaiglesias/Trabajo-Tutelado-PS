package es.udc.cookbook.Recipes;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.HashSet;
import java.util.Set;

import es.udc.cookbook.R;

public class RecipeDetail extends AppCompatActivity {
    boolean isLiked = false;

    SharedPreferences preferences;
    String user;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);

        TextView creator = findViewById(R.id.creatorRecipe);

        TextView titleDt = findViewById(R.id.TitleDetail);
        TextView recipeInfo = findViewById(R.id.RecipeInfo);
        ImageView imageDt = findViewById(R.id.ImageDetail);

        //Recuperamos la información de la receta
        String title = getIntent().getStringExtra("title");
        String image = getIntent().getStringExtra("image");
        String ingredients = getIntent().getStringExtra("ingredients");
        String instructions = getIntent().getStringExtra("instructions");
        user = getIntent().getStringExtra("user");

        ImageButton likeButton = findViewById(R.id.likeButton);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLiked = !isLiked;
                if (isLiked) {
                    likeButton.setImageResource(R.drawable.ic_active_like);
                } else {
                    likeButton.setImageResource(R.drawable.ic_inactive_like);
                }
            }
        });

        //Mostramos la imagen
        showImage(imageDt, image);
        //Mostramos el Título
        titleDt.setText(title);
        creator.setText(user);
        //Mostramos ingredientes
        ingredients = ingredients.substring(1, ingredients.length() - 1);
        String[] elements = ingredients.split(", ");
        StringBuilder output = new StringBuilder();
        for (String element : elements) {
            element = element.substring(1, element.length() - 1);
            output.append("- ").append(element).append("\n");
        }
        String result = output.toString();
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
                        recipeInfo.setText(instructions);
                        return true;
                }
                return false;
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

    private void showImage(ImageView imageView, String uri){
        Glide.with(getApplicationContext())
                .load(uri)
                .into(imageView);
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
}
