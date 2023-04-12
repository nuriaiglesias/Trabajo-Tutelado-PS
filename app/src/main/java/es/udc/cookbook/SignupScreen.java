package es.udc.cookbook;

import android.content.Intent;
import android.os.Bundle;
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

public class SignupScreen extends AppCompatActivity {

    EditText email, username, password, password2;
    Button SignupButton;
    DatabaseReference databaseReference;
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
                            Toast.makeText(SignupScreen.this, "El campo de texto está vacío", Toast.LENGTH_LONG).show();
                        } else if(snapshot.exists()){
                            Toast.makeText(SignupScreen.this, "El usuario ya está usado, cambie por otro por favor", Toast.LENGTH_LONG).show();
                        }else if(!contrasena.equals(contrasena2)){
                            Toast.makeText(SignupScreen.this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                        }else{
                            // Registro de datos en la BD
                            Usuario usuario = new Usuario(correo, nombre, contrasena);
                            databaseReference.child(nombre).setValue(usuario);

                            Intent intent = new Intent(SignupScreen.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignupScreen.this, "Registro exitoso!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Se produjo un error al intentar leer los datos
                        Toast.makeText(SignupScreen.this, "Error al leer la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    public static class Usuario {
        public String correo;
        public String nombre;
        public String contrasena;

        public Usuario(String correo, String nombre, String contrasena) {
            this.correo = correo;
            this.nombre = nombre;
            this.contrasena = contrasena;
        }
    }
}
