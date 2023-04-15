package es.udc.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import es.udc.cookbook.Recipes.Recipe;
import es.udc.cookbook.Recipes.RecipeAdapter;


public class MainActivity extends AppCompatActivity {
    private ArrayList<Recipe> recipes;
    RecyclerView recyclerView;
    //Firebase
    private DatabaseReference ref;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Firebase
        ref = FirebaseDatabase.getInstance().getReference();

        recipes = new ArrayList<>();
        
        GetDataFromFirebase();
        

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String key = snapshot.getKey();
                    String ingredients = snapshot.child("Cleaned_Ingredients").getValue(String.class);
                    String image = snapshot.child("Imagen_Name").getValue(String.class);
                    String instructions = snapshot.child("Instructions").getValue(String.class);
                    String title = snapshot.child("Title").getValue(String.class);
                    Recipe recipe = new Recipe(ingredients, image, instructions, title, key);
                    recipes.add(recipe);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Recipes", "Error", databaseError.toException());
            }
        });
    }

    private void GetDataFromFirebase() {
        Query query = ref.child("Recetas");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                ClearAll();
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    Recipe recipe = new Recipe();
                    recipe.setImage(snapshot.child("Image_Name").getValue().toString());
                    recipe.setTitle(snapshot.child("Title").getValue().toString());
                    recipes.add(recipe);
                }
                recipeAdapter = new RecipeAdapter(getApplicationContext(),recipes);
                recyclerView.setAdapter(recipeAdapter);
                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void ClearAll(){
        if(recipes != null){
            recipes.clear();
            if(recipeAdapter != null){
                recipeAdapter.notifyDataSetChanged();
            }
        }
        recipes = new ArrayList<>();

    }
}
