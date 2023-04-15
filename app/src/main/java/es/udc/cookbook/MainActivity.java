package es.udc.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import es.udc.cookbook.Recipes.Recipe;
import es.udc.cookbook.Recipes.RecipeAdapter;


public class MainActivity extends AppCompatActivity {
    private List<Recipe> imageList;
    private RecipeAdapter imageAdapter;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageList = new ArrayList<>();
        imageAdapter = new RecipeAdapter(imageList);
        recyclerView.setAdapter(imageAdapter);

        // Se obtiene una referencia a la base de datos de Firebase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("FoodImages");

        // Se agrega un ValueEventListener para escuchar cambios en la lista de recetas
        imagesRef.listAll().addOnSuccessListener(listResult -> {
            int numImages = listResult.getItems().size();
            final int[] counter = {0}; // variable contador
            for (int i = 0; i < numImages; i++) {
                StorageReference item = listResult.getItems().get(i);
                // Obtener el URL de la imagen
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Log.d("MainActivity", "Image URL: " + imageUrl);

                    // Crear una nueva instancia de Recipe con el URL de la imagen
                    Recipe recipe = new Recipe(imageUrl);

                    // Agregar la receta a la lista
                    imageList.add(recipe);

                    // Aumentar el contador en uno
                    counter[0]++;
                    imageAdapter.notifyDataSetChanged();
                    imageAdapter.updateImageList(imageList);
                    Log.d("MainActivity", "Error loading image: " + imageList.size());
                }).addOnFailureListener(e -> {
                    // Manejar la excepción
                    Log.d("MainActivity", "Error loading image: " + e.getMessage() + ", item name: " + item.getName());                                });
                // Salir del bucle si se han obtenido todas las URL
                if (counter[0] == numImages) break;
            }

            Log.d("MainActivity", "Recipe list size: ");
        })
                .addOnFailureListener(e -> {
                    // Manejar la excepción
                    Log.d("MainActivity", "Error loading image: item");
                });
    }
}
