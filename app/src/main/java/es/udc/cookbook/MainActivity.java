package es.udc.cookbook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import es.udc.cookbook.Recipes.Recipe;
import es.udc.cookbook.Recipes.RecipeAdapter;

import es.udc.cookbook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

        // Obtiene una referencia a la imagen en Firebase Storage
        String imageName = "chicken-and-rice-with-leeks-and-salsa-verde.jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages").child(imageName);

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            DatabaseReference ref = FirebaseDatabase.getInstance("https://cookbook-8f52e-default-rtdb.europe-west1.firebasedatabase.app").getReference();

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        List<Recipe> recipes = new ArrayList<>();
                        String key = snapshot.getKey();
                        String ingredients = snapshot.child("Cleaned_Ingredients").getValue(String.class);
                        String image = snapshot.child("Imagen_Name").getValue(String.class);
                        String instructions = snapshot.child("Instructions").getValue(String.class);
                        String title = snapshot.child("Title").getValue(String.class);
                        Recipe recipe = new Recipe(ingredients, image, instructions, title, key);
                        recipes.add(recipe);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejamos el error de lectura de datos
                    Log.e("Recetas", "Error al leer datos", databaseError.toException());
                }
            });
        }

    }