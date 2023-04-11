package es.udc.cookbook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import es.udc.cookbook.Recipes.Recipe;
import es.udc.cookbook.Recipes.RecipeAdapter;

import es.udc.cookbook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private RecipeAdapter mAdapter;
    public static final String KEY_POSITION = "position" ;
    public static String KEY_RECIPE = "recipe";
    private ActivityMainBinding binding;


    // Obtiene una referencia a la imagen en Firebase Storage
    String imageName = "chicken-and-rice-with-leeks-and-salsa-verde.jpg";
    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages").child(imageName);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView image = findViewById(R.id.my_image_view);
        Context context = image.getContext();

        DatabaseReference ref = FirebaseDatabase.getInstance("https://cookbook-8f52e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Carga la imagen en un ImageView usando Glide
                Glide.with(context)
                        .load(uri)
                        .into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Maneja cualquier error al descargar la imagen
            }
        });

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Recorremos todos los hijos de la ubicación de las recetas
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtenemos la clave de la receta actual
                    String key = snapshot.getKey();
                    // Hacemos algo con la clave de la receta
                    Log.d("Receta", "Clave: " + key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejamos el error de lectura de datos
                Log.e("Recetas", "Error al leer datos", databaseError.toException());
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

}