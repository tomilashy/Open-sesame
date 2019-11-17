package com.project.coen_elec_390;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PeekActivity extends AppCompatActivity {

    private ImageView peekImage;

    private SharedPreferences sharedPreference;
    private DatabaseHelper databaseHelper;
    private FirebaseFirestore database;

    private int doorID;

    private final String TAG = "HISTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peek);
        setTitle("Peek Door");

        peekImage = findViewById(R.id.peekImage);

        databaseHelper = new DatabaseHelper();

        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        doorID = sharedPreference.getInt("doorID", 0);
        database = databaseHelper.getDatabase();

        database.collection("doors")
                .document(Integer.toString(doorID))
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
    }
}
