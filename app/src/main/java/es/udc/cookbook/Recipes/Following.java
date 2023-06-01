package es.udc.cookbook.Recipes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.udc.cookbook.R;

public class Following extends AppCompatActivity {

    RecyclerView recyclerView;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        recyclerView = findViewById(R.id.recycleView3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Obtén el nombre de usuario del usuario conectado
        preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        String loggedInUser = preferences.getString("username", "");

        // Obtén el conjunto de usuarios seguidos del usuario conectado
        Set<String> followedUsers = preferences.getStringSet(loggedInUser + "_followed_users", new HashSet<>());

        List<String> userList = new ArrayList<>(followedUsers);
        // Configura el adaptador del RecyclerView
        FollowingAdapter adapter = new FollowingAdapter(userList);
        adapter.setOnUnfollowClickListener(new FollowingAdapter.OnUnfollowClickListener() {
            @Override
            public void onUnfollowClick(int position) {
                // Elimina el usuario de la lista de seguidos
                String unfollowedUser = userList.get(position);
                userList.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(Following.this, "Stopped following " + unfollowedUser, Toast.LENGTH_SHORT).show();

                // Actualiza el conjunto de usuarios seguidos en el SharedPreferences
                Set<String> updatedFollowedUsers = new HashSet<>(userList);
                preferences.edit().putStringSet(loggedInUser + "_followed_users", updatedFollowedUsers).apply();
            }
        });
        recyclerView.setAdapter(adapter);


        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar4);
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}