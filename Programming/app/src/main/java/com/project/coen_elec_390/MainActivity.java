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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ImageButton peek;
    private ImageButton unlock;
    private ImageButton admins;
    private ImageButton history;
    private ImageButton logout;
    private Toast toast;

    private SharedPreferences sharedPreference;
    private DatabaseHelper databaseHelper;

    private Handler handler;

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
        logout = findViewById(R.id.logout);

        handler = new Handler();

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
                        .update("isDoorConnected", false)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Unlock Door")
                                        .setMessage("Are you sure you want to unlock the door?")

                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                database.collection("doors")
                                                        .document(sDoorID)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    // Document found in the offline cache
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.getData().get("isDoorConnected").toString().equals("true")) {
                                                                        database.collection("doors")
                                                                                .document(sDoorID)
                                                                                .update("lock", false)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        toast = Toast.makeText(MainActivity.this, "Door successfully unlocked!", Toast.LENGTH_LONG);
                                                                                        toast.show();

                                                                                        Log.d(TAG, "Door successfully unlocked!");
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w(TAG, "Error updating document ", e);
                                                                            }
                                                                        });
                                                                    } else {
                                                                        toast = Toast.makeText(MainActivity.this, "Door is not connected!", Toast.LENGTH_LONG);
                                                                        toast.show();
                                                                    }
                                                                } else {
                                                                    Log.d(TAG, "get failed: ", task.getException());
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document ", e);
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
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });

        String prevTopic = sharedPreference.getString("topic", "DEFAULT");
        if (prevTopic.equals("DEFAULT")) {
            addTopicToDevice(sDoorID);
        } else {
            if (!prevTopic.equals(sDoorID)) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(prevTopic);
                addTopicToDevice(sDoorID);
            }
        }

        handler.postDelayed(networkCheck, 0);
    }

    Runnable networkCheck = new Runnable() {
        @Override
        public void run() {

            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onStop() {
        handler.removeCallbacksAndMessages(null);
        super.onStop();
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
            case R.id.goToCredits:
                goToCredits();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void addTopicToDevice(final String sDoorID) {
        FirebaseMessaging.getInstance().subscribeToTopic(sDoorID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences.Editor editor = sharedPreference.edit();
                        editor.remove("topic");
                        editor.putString("topic", sDoorID);
                        editor.commit();

                        toast = Toast.makeText(MainActivity.this, "Subscribed to door: " + doorID, Toast.LENGTH_LONG);
                        toast.show();

                        Log.d(TAG, "Subscribed to door: " + doorID);
                    }
                });
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
