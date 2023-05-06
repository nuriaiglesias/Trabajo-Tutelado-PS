package es.udc.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;

import es.udc.cookbook.Recipes.Recipe;


public class AddRecipe extends AppCompatActivity {

    private Button addRecipeButton; // para añadir la receta a la BD
    private EditText titleNewRecipe;
    private EditText ingredientsNewRecipe;
    private EditText instructionNewRecipe;
    private EditText titleImageNewRecipe;
    StorageReference storageRef = null;
    DatabaseReference databaseReference = null;
    AlertDialog dialog;

    private final int PICK_IMAGE_GALLERY_CODE = 78;
    private static final int SELECT_IMAGE_REQUEST = 1;

    private final int CAMERA_PERMISSION_REQUEST_CODE = 12345;

    private final int CAMERA_PICTURE_REQUEST_CODE = 5678;
    private Uri filePath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);
        addRecipeButton = findViewById(R.id.addRecipe);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        titleNewRecipe = findViewById(R.id.titleNewRecipe);
        ingredientsNewRecipe = findViewById(R.id.ingredientsNewRecipe);
        instructionNewRecipe = findViewById(R.id.instructionNewRecipe);
        titleImageNewRecipe = findViewById(R.id.titleImageNewRecipe);
        storageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recetas");
    }

    public void onBack(View view){
        onBackPressed();
    }

    // Para seleccionar la imagen
    public void selectImage(View view) {
        showImagesSelectedDialog();
    }

    private void showImagesSelectedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select image");
        builder.setMessage("Please select an option");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkCameraPermission();
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectFromGallery();
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void checkCameraPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)  != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            },CAMERA_PERMISSION_REQUEST_CODE);
        }else{
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            openCamera();
        }
    }

    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(intent,CAMERA_PICTURE_REQUEST_CODE);
        }
    }

    private void selectFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image"), PICK_IMAGE_GALLERY_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            if(data == null || data.getData() == null){
                return;
            }
            try {
                filePath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else if(requestCode == CAMERA_PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            filePath = getImageUri(getApplicationContext(),bitmap);
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"title",null);
        return Uri.parse(path);
    }
    public void addRecipe(View view){
        uploadImage();
    }

    private void uploadImage(){
        if(filePath!= null){
            String titulo = titleNewRecipe.getText().toString();
            String ingredientes = ingredientsNewRecipe.getText().toString();
            String instrucciones = instructionNewRecipe.getText().toString();
            String tituloImagen = titleImageNewRecipe.getText().toString();
            tituloImagen += ".jpg";
            StorageReference ref = storageRef.child("FoodImages/" + tituloImagen);
            String finalTituloImagen = tituloImagen.substring(0, tituloImagen.length() - 4);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (titulo.length() == 0|| ingredientes.length() == 0 || instrucciones.length() == 0 || finalTituloImagen.length() == 0) {
                                Toast.makeText(AddRecipe.this, "El campo de texto está vacío", Toast.LENGTH_LONG).show();
                            }else{
                                // Registro de datos en la BD
                                Recipe receta = new Recipe(ingredientes, finalTituloImagen,instrucciones,titulo,null);
                                receta.setImageLoaded(true);
                                databaseReference.child(titulo).setValue(receta);
                                Toast.makeText(AddRecipe.this,"Receta añadida",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddRecipe.this,"Algo ha fallado!",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}