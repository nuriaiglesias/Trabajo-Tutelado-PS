package es.udc.cookbook.Pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import es.udc.cookbook.R;
import java.util.List;
import es.udc.cookbook.Users.User;


public class SignupScreen extends AppCompatActivity {

    EditText email, username, password, password2;
    Button SignupButton;
    DatabaseReference databaseReference;
    List<String> favRecipes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        SignupButton = findViewById(R.id.signupButtonScreen);
        email = findViewById(R.id.email);
        username = findViewById(R.id.usernameSignup);
        password = findViewById(R.id.passwordSignup);
        password2 = findViewById(R.id.password2Signup);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = email.getText().toString().trim();
                String nombre = username.getText().toString().trim();
                String contrasena = password.getText().toString().trim();
                String contrasena2 = password2.getText().toString().trim();

                databaseReference.child(nombre).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (correo.length() == 0|| nombre.length() == 0 || contrasena.length() == 0 || contrasena2.length() == 0) {
                            String campoVacio = getString(R.string.mensaje_vacio);
                            Toast.makeText(SignupScreen.this, campoVacio, Toast.LENGTH_LONG).show();
                        }else if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                            String emailIncorrecto = getString(R.string.emailIncorrecto);
                            Toast.makeText(SignupScreen.this, emailIncorrecto, Toast.LENGTH_SHORT).show();
                        }else if(snapshot.exists()){
                            String usuarioRep = getString(R.string.usuarioRep);
                            Toast.makeText(SignupScreen.this, usuarioRep, Toast.LENGTH_LONG).show();
                        }else if(!contrasena.equals(contrasena2)){
                            String passwords = getString(R.string.passwords);
                            Toast.makeText(SignupScreen.this, passwords, Toast.LENGTH_LONG).show();
                        }else{
                            // Registro de datos en la BD
                            User usuario = new User(correo, nombre, contrasena, favRecipes);
                            databaseReference.child(nombre).setValue(usuario);

                            SharedPreferences preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", nombre);
                            editor.apply();

                            Intent intent = new Intent(SignupScreen.this, MainActivity.class);
                            startActivity(intent);
                            String registroExitoso = getString(R.string.registroE);
                            Toast.makeText(SignupScreen.this, registroExitoso, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignupScreen.this, "Error at the time of reading the database", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

}
