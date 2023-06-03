package es.udc.cookbook.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.udc.cookbook.R;

public class Settings extends AppCompatActivity {
    SharedPreferences preferences;
    DatabaseReference ref = null;
    String username;
    EditText nombreSettings;
    EditText emailSettings;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nombreSettings = findViewById(R.id.usernameSettings);
        emailSettings = findViewById(R.id.emailSettings);

        ref = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        username = preferences.getString("username", "");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(username).exists()) {
                    // Obtener los datos del usuario
                    DataSnapshot userSnapshot = dataSnapshot.child(username);
                    String nombre = userSnapshot.child("name").getValue(String.class);
                    String email = userSnapshot.child("mail").getValue(String.class);
                    nombreSettings.setText(nombre);
                    emailSettings.setText(email);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error al obtener los datos del usuario
            }
        });

        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar3);
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    public void changePassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.changePass));
        // Crear el LinearLayout contenedor
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Crear el EditText para la contraseña actual
        EditText currentPasswordEditText = new EditText(this);
        currentPasswordEditText.setHint(getString(R.string.actualPass));
        currentPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Crear el EditText para la nueva contraseña
        EditText newPasswordEditText = new EditText(this);
        newPasswordEditText.setHint(getString(R.string.newPass));
        newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Agregar los EditText al LinearLayout
        layout.addView(currentPasswordEditText);
        layout.addView(newPasswordEditText);
        builder.setView(layout);

        builder.setPositiveButton(getString(R.string.change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String currentPassword = currentPasswordEditText.getText().toString().trim();
                String nuevaContrasena = newPasswordEditText.getText().toString().trim();

                DatabaseReference usuarioRef = ref.child(username);
                usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Verificar si el usuario existe en la base de datos
                        if (dataSnapshot.exists()) {
                            String contrasena = dataSnapshot.child("password").getValue(String.class);
                            if(currentPassword.equals(contrasena)){
                                usuarioRef.child("password").setValue(nuevaContrasena);
                                Toast.makeText(Settings.this, getString(R.string.cambioContrasena), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Settings.this, getString(R.string.contraseñaIncorrecta), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Settings.this, getString(R.string.errorUserdata), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        builder.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void saveChanges(View view) {
        String nuevoNombre = nombreSettings.getText().toString();
        String nuevoEmail = emailSettings.getText().toString();

        DatabaseReference usuarioRef = ref.child(username);
        usuarioRef.child("name").setValue(nuevoNombre);
        usuarioRef.child("mail").setValue(nuevoEmail);
    }

    public void logout(View view) {
        // Volver a la actividad de inicio de sesión
        Intent intent = new Intent(this, LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void deleteAccount(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.delete_account));
        alertDialog.setMessage(getString(R.string.alertDelete));

        if(username.equals("CookBook")) {
            alertDialog.setMessage(getString(R.string.noDelete));
            alertDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else {
            alertDialog.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Eliminar la cuenta del usuario
                    DatabaseReference usuarioRef = ref.child(username);
                    usuarioRef.removeValue();

                    // Eliminar los datos almacenados en SharedPreferences
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();

                    // Eliminar las recetas asociadas al usuario
                    DatabaseReference recetasRef = FirebaseDatabase.getInstance().getReference("Recetas");
                    Query recetasQuery = recetasRef.orderByChild("user").equalTo(username);
                    recetasQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                                recipeSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Settings.this, getString(R.string.errorDeleterecipes), Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Volver a la actividad de inicio de sesión
                    Intent intent = new Intent(Settings.this, LoginScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

}