package es.udc.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import es.udc.cookbook.Recipes.Recipe;


public class AddRecipe extends AppCompatActivity {

    private Button cancelButton;
    private Button addRecipeButton;
    private static final int REQUEST_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private EditText titleNewRecipe;
    private EditText ingredientsNewRecipe;
    private EditText instructionNewRecipe;
    private EditText titleImageNewRecipe;
    private Uri imageUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Recetas");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);
        cancelButton = findViewById(R.id.goBackButton);
        addRecipeButton = findViewById(R.id.addRecipe);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        titleNewRecipe = findViewById(R.id.titleNewRecipe);
        ingredientsNewRecipe = findViewById(R.id.ingredientsNewRecipe);
        instructionNewRecipe = findViewById(R.id.instructionNewRecipe);
        titleImageNewRecipe = findViewById(R.id.titleImageNewRecipe);

        addRecipe();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    public void onBack(View view){
        onBackPressed();
    }
    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public void addRecipe(){
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = titleNewRecipe.getText().toString();
                String ingredientes = ingredientsNewRecipe.getText().toString();
                String instrucciones = instructionNewRecipe.getText().toString();
                String tituloImagen = titleImageNewRecipe.getText().toString();
                databaseReference.child(titulo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (titulo.length() == 0|| ingredientes.length() == 0 || instrucciones.length() == 0 || tituloImagen.length() == 0) {
                            Toast.makeText(AddRecipe.this, "El campo de texto está vacío", Toast.LENGTH_LONG).show();
                        }else if (snapshot.exists()) {
                            Toast.makeText(AddRecipe.this, "Este título ya existe", Toast.LENGTH_LONG).show();
                        }else if(imageUri == null){
                            Toast.makeText(AddRecipe.this, "Añade una imagen", Toast.LENGTH_LONG).show();
                        }else{
                            // Registro de datos en la BD
                            Recipe receta = new Recipe(ingredientes, tituloImagen,instrucciones,titulo,null);
                            databaseReference.child(titulo).setValue(receta);

                            Intent intent = new Intent(AddRecipe.this, UserRecipes.class);
                            startActivity(intent);
                            Toast.makeText(AddRecipe.this, "Añadida tu receta!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Se produjo un error al intentar leer los datos
                        Toast.makeText(AddRecipe.this, "Error al leer la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });

                if(imageUri != null){
                    StorageReference imageRef = storageRef.child("FoodImages/" + tituloImagen);
                    // Carga la imagen en Firebase Storage
                    UploadTask uploadTask = imageRef.putFile(imageUri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddRecipe.this, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Se produjo un error al cargar la imagen en Firebase Storage
                            Toast.makeText(AddRecipe.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
        }
    }
}