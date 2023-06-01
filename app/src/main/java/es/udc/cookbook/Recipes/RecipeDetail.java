package es.udc.cookbook.Recipes;
import static es.udc.cookbook.Recipes.Recipe.getRecipeById;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import es.udc.cookbook.R;

public class RecipeDetail extends AppCompatActivity {
    Recipe recipe;
    DatabaseReference ref;
    boolean isLiked = false;
    // Recuperamos nombre usuario actual
    SharedPreferences preferences;
    String username;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);

        TextView titleDt = findViewById(R.id.TitleDetail);
        TextView recipeInfo = findViewById(R.id.RecipeInfo);
        ImageView imageDt = findViewById(R.id.ImageDetail);

        //Recuperamos la información de la receta
        String recipeId = getIntent().getStringExtra("id");

        //Botón para dar "like"
        ImageButton likeButton = findViewById(R.id.likeButton);
        //Recuperamos referencia a bd
        ref = FirebaseDatabase.getInstance().getReference();
        //Recuperamos info del usuario
        preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        username = preferences.getString("username", "");
        /*
        if (!username.isEmpty()) {
            TextView usernameTextView = findViewById(R.id.user_name);
            usernameTextView.setText(username);
        } else {
            Toast.makeText(getApplicationContext(),"No detectado el nombre correctamente", Toast.LENGTH_LONG).show();
        }

         */

        // Obtenemos la info de la recta a través del ID
        Recipe.getRecipeById(recipeId, new Recipe.RecipeCallback() {
            @Override
            public void onRecipeLoaded(Recipe recipe) {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaa" + recipe.imageName);
                System.out.println("por dios que funcione" + recipeId + recipe.ingredients + recipe.uriRecipe);

                //Mostramos la imagen
                showImage(imageDt, recipe.imageName);
                //Mostramos el Título
                titleDt.setText(recipe.title);
                //Mostramos ingredientes
                recipe.ingredients = recipe.ingredients.substring(1, recipe.ingredients.length() - 1);
                String[] elements = recipe.ingredients.split(", ");
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
                                recipeInfo.setText(recipe.instructions);
                                return true;
                        }
                        return false;
                    }
                });

            }

            @Override
            public void onError(DatabaseError databaseError) {
                // Manejar el error de obtención de la receta
            }
        });




        //Marca las fotos como favoritas
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLiked = !isLiked;
                if (isLiked) {
                    likeButton.setImageResource(R.drawable.ic_active_like);
                    ref.child("Usuarios").orderByChild("nombre").equalTo(username);

                } else {
                    likeButton.setImageResource(R.drawable.ic_inactive_like);
                }
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

    private void showImage(ImageView imageView, String uriR){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages");
        storageRef.child(uriR).getDownloadUrl().addOnSuccessListener(uri -> {
            if (uri != null) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(e -> Log.d("RecipeAdapter", "Failed to load image"));
    }

    private void addRecipetoFav() {
        ref.child("Usuarios").orderByChild("nombre").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();

                    // Obtener el ID de la receta seleccionada
                    String recetaId = "ID_DE_LA_RECETA_A_AGREGAR_A_FAVORITOS";

                    // Guardar la receta favorita en la colección "favoritos" del usuario
                    ref.child("Usuarios").child(userId).child("favoritos").child(recetaId).setValue(true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // La receta favorita se guardó exitosamente en el usuario
                                        Toast.makeText(getApplicationContext(), "Receta agregada a favoritos", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Ocurrió un error al guardar la receta favorita en el usuario
                                        Toast.makeText(getApplicationContext(), "Error al agregar la receta a favoritos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Ocurrió un error al obtener los datos del usuario
            }
        });
    }



}
