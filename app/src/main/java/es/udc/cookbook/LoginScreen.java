package es.udc.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginScreen extends AppCompatActivity{

    EditText username, password;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                startActivity(intent);
                if(username.getText().toString().equals("user") && password.getText().toString().equals("hola")){
                    Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginScreen.this, "arolas", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
