package com.project.coen_elec_390;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button peek;
    private Button unlock;
    private Button admins;
    private Button history;
    private Button credits;
    private Button logout;

    private SharedPreferences sharedPreference;
    private DatabaseHelper databaseHelper;

    private int doorID;

    private String TAG = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Open Sesame");

        peek = findViewById(R.id.peek);
        unlock = findViewById(R.id.unlock);
        admins = findViewById(R.id.admins);
        history = findViewById(R.id.history);
        credits = findViewById(R.id.credits);
        logout = findViewById(R.id.logout);

        databaseHelper = new DatabaseHelper();
        final FirebaseFirestore database = databaseHelper.getDatabase();

        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        doorID = sharedPreference.getInt("doorID", 0);
        final String sDoorID = Integer.toString(doorID);

        peek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToPeek();
            }
        });
        unlock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                database.collection("doors")
                        .document(sDoorID)
                        .update("lock", false)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Door unlocked!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
            }
        });
        admins.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToAdministrators();
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToHistory();
            }
        });
        credits.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToCredits();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfile:
                editProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void goToPeek() {
        startActivity(new Intent(this, PeekActivity.class));
    }

    private void goToAdministrators() {
        startActivity(new Intent(this, AdminsActivity.class));
    }

    private void goToHistory() {
        startActivity(new Intent(this, DisplayHistory.class));
    }

    private void goToCredits() {
        startActivity(new Intent(this, Credits.class));
    }

    private void editProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void logout() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
