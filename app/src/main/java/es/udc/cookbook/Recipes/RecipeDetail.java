package es.udc.cookbook.Recipes;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.udc.cookbook.LikedRecipes;
import es.udc.cookbook.R;
import es.udc.cookbook.UserRecipes;

public class RecipeDetail extends AppCompatActivity {
    boolean isLiked = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);

        TextView titleDt = findViewById(R.id.TitleDetail);
        TextView recipeInfo = findViewById(R.id.RecipeInfo);
        ImageView imageDt = findViewById(R.id.ImageDetail);

        //Recuperamos la información de la receta
        String title = getIntent().getStringExtra("title");
        String image = getIntent().getStringExtra("image");
        String ingredients = getIntent().getStringExtra("ingredients");
        String instructions = getIntent().getStringExtra("instructions");

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

}
