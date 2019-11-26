package com.project.coen_elec_390;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminsActivity extends AppCompatActivity {

    private RecyclerView adminsView;
    private ProfileAdapter profileAdapter;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreference;

    private Handler handler;
    private boolean check;

    private ArrayList<Profile> profiles;

    private final String TAG = "ADMINS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins);
        setTitle("Administrators");

        adminsView = findViewById(R.id.adminsView);
        adminsView.setLayoutManager(new LinearLayoutManager(this));

        //Add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper();
        sharedPreference = this.getSharedPreferences("ProfilePreference",
                this.MODE_PRIVATE);

        profiles = new ArrayList<>();

        handler = new Handler();
        check = true;

        final FirebaseFirestore database = databaseHelper.getDatabase();
        final int doorID = sharedPreference.getInt("doorID", 0);

        database.collection("profiles")
                .whereEqualTo("doorID", doorID )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                profiles.add(new Profile(
                                        document.getData().get("username").toString(),
                                        document.getData().get("phoneNum").toString(),
                                        document.getData().get("password").toString(),
                                        Integer.parseInt(document.getData().get("doorID").toString()),
                                        document.getData().get("imageUrl").toString()));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            profileAdapter = new ProfileAdapter(AdminsActivity.this, profiles);
                            adminsView.setAdapter(profileAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        handler.postDelayed(networkCheck, 0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish(); // When home button in clicked, end the activity and return to MainActivity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Runnable networkCheck = new Runnable() {
        @Override
        public void run() {

            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(AdminsActivity.this.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                if (check) {
                    check = false;
                    startActivity(new Intent(AdminsActivity.this, LoginActivity.class));
                }
            }

            handler.postDelayed(this, 500);
        }
    };
}
