package com.project.coen_elec_390;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PeekActivity extends AppCompatActivity {

    private ImageView peekImage;
    private ImageButton unlock;

    private SharedPreferences sharedPreference;
    private DatabaseHelper databaseHelper;
    private FirebaseFirestore database;

    private Handler handler;
    private boolean check;

    private int doorID;

    private final String TAG = "HISTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peek);
        setTitle("Check Door");

        peekImage = findViewById(R.id.peekImage);
        unlock = findViewById(R.id.unlockDoor);

        databaseHelper = new DatabaseHelper();

        //Add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        doorID = sharedPreference.getInt("doorID", 0);
        database = databaseHelper.getDatabase();

        handler = new Handler();
        check = true;

        final String sDoorID = Integer.toString(doorID);

        database.collection("doors")
                .document(sDoorID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Picasso.with(PeekActivity.this)
                                        .load(document.getString("lastImageUrl"))
                                        .fit()
                                        .into(peekImage);
                            } else {
                                Log.d(TAG, "No such document exists!");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

        unlock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(PeekActivity.this)
                        .setTitle("Unlock Door")
                        .setMessage("Are you sure you want to unlock the door?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                database.collection("doors")
                                        .document(sDoorID)
                                        .update("lock", false)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PeekActivity.this, "Door successfully unlocked!", Toast.LENGTH_LONG).show();

                                                Log.d(TAG, "Door successfully unlocked!");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document ", e);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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
                    = (ConnectivityManager) getSystemService(PeekActivity.this.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                if (check) {
                    check = false;
                    startActivity(new Intent(PeekActivity.this, LoginActivity.class));
                }
            }

            handler.postDelayed(this, 500);
        }
    };
}
