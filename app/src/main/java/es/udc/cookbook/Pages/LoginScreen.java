package es.udc.cookbook.Pages;

import android.content.Intent;
import android.content.SharedPreferences;
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

import es.udc.cookbook.R;

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
                            String mensajeVacio = getString(R.string.mensaje_vacio);
                            Toast.makeText(LoginScreen.this, mensajeVacio, Toast.LENGTH_LONG).show();
                        } else if(snapshot.exists()){
                            String passwordFromDatabase = snapshot.child("password").getValue(String.class);
                            if (contrasena.equals(passwordFromDatabase)) {
                                String usernameFromDatabase = snapshot.child("name").getValue(String.class);
                                SharedPreferences preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("username", usernameFromDatabase);
                                editor.apply();
                                Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                                startActivity(intent);
                                String acceso = getString(R.string.acceso);
                                Toast.makeText(LoginScreen.this,acceso, Toast.LENGTH_SHORT).show();
                            } else {
                                String contrasenaIncorrecta = getString(R.string.contraseñaIncorrecta);
                                Toast.makeText(LoginScreen.this, contrasenaIncorrecta, Toast.LENGTH_LONG).show();
                            }
                        }else {
                            String noRegistrado = getString(R.string.noRegistrado);
                            Toast.makeText(LoginScreen.this, noRegistrado, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Se produjo un error al intentar leer los datos
                        Toast.makeText(LoginScreen.this, "Error at the time of reading the database", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this, SignupScreen.class);
                startActivity(intent);
            }
        });
    }
}
