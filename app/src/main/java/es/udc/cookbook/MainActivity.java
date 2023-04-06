package es.udc.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://cookbook-8f52e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
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

}