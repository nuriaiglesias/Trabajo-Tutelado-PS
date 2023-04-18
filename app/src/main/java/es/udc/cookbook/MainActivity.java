package es.udc.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import es.udc.cookbook.Recipes.RecipeDetail;


public class MainActivity extends AppCompatActivity {
    private final ArrayList<Recipe> recipes = new ArrayList<>();

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
        GetDataFromFirebase();
    }

    private void GetDataFromFirebase() {
        Query query = ref.child("Recetas");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    Recipe recipe = new Recipe();
                    recipe.setImage(snapshot.child("Image_Name").getValue().toString());
                    System.out.println(snapshot.child("Image_Name").getValue().toString());
                    Log.d("_tag", " imagen" + snapshot.child("Image_Name").getValue().toString());
                    recipe.setTitle(snapshot.child("Title").getValue().toString());
                    recipe.setInstructions(snapshot.child("Instructions").getValue().toString());
                    recipe.setIngredients(snapshot.child("Cleaned_Ingredients").getValue().toString());
                    recipes.add(recipe);
                }
                recipeAdapter = new RecipeAdapter(getApplicationContext(),recipes);
                recyclerView.setAdapter(recipeAdapter);

                recipeAdapter.setClickListener(new RecipeAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Log.d("_TAG", " Item " + recipes.get(position).image );
                        Toast.makeText(getApplicationContext(), "item " +  recipes.get(position).image ,
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, RecipeDetail.class);
                        intent.putExtra("title", recipes.get(position).title);
                        intent.putExtra("image", recipes.get(position).image);
                        intent.putExtra("instructions", recipes.get(position).instructions);
                        intent.putExtra("ingredients", recipes.get(position).ingredients);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
