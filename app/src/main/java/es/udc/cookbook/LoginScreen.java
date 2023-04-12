package es.udc.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginScreen extends AppCompatActivity{

    EditText username, password;
    Button loginButton;
    DatabaseReference databaseReference;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = username.getText().toString().trim();
                String contrasena = password.getText().toString().trim();

                databaseReference.child(nombre).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (nombre.length() == 0|| contrasena.length() == 0) {
                            Toast.makeText(LoginScreen.this, "El campo de texto está vacío", Toast.LENGTH_LONG).show();
                        } else if(snapshot.exists()){
                            Usuario usuario = new Usuario(nombre, contrasena);
                            databaseReference.child(nombre).setValue(usuario);

                            Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginScreen.this, "Acceso exitoso!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(LoginScreen.this, "El usuario aún no está registrado", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Se produjo un error al intentar leer los datos
                        Toast.makeText(LoginScreen.this, "Error al leer la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = username.getText().toString().trim();
                String contrasena = password.getText().toString().trim();

                databaseReference.child(nombre).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (nombre.length() == 0 || contrasena.length() == 0) {
                            Toast.makeText(LoginScreen.this, "El campo de texto está vacío", Toast.LENGTH_LONG).show();
                        } else if(snapshot.exists()){
                            Toast.makeText(LoginScreen.this, "El usuario ya está registrado", Toast.LENGTH_LONG).show();
                        } else {
                            // Registro de datos en la BD
                            Usuario usuario = new Usuario(nombre, contrasena);
                            databaseReference.child(nombre).setValue(usuario);

                            Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginScreen.this, "Registro exitoso!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginScreen.this, "Error al leer la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public static class Usuario {
        public String nombre;
        public String contrasena;

        public Usuario(String nombre, String contrasena) {
            this.nombre = nombre;
            this.contrasena = contrasena;
        }
    }
}
