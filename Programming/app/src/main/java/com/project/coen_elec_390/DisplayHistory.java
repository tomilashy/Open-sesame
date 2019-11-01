package com.project.coen_elec_390;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import static androidx.constraintlayout.widget.Constraints.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class DisplayHistory extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;

    private DatabaseReference mDatabaseRef;
    private SharedPreferences mSharedPreference;
    private DatabaseHelper mDatabaseHelper;
    private FirebaseFirestore mDatabase;

    private List<ImageInfo> mListImageInfo;
    private int mDoorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_history);

        mDatabaseHelper = new DatabaseHelper();
        mDatabase = mDatabaseHelper.getDatabase();

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayHistory.this));
        mRecyclerView.setHasFixedSize(true);

        mProgressCircle = findViewById(R.id.progress_circle);

        mSharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        mDoorID = mSharedPreference.getInt("doorID", 0);
        mListImageInfo = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("door_" + mDoorID + "/history");
        setTitle("Door " + mDoorID + " History");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase.collection("door_" + mDoorID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        mListImageInfo.add(new ImageInfo(document.getData().get("imageName").toString()
                                                , document.getData().get("imageUrl").toString()
                                                , document.getData().get("doorID").toString()));
                                    }
                                    mAdapter = new ImageAdapter(DisplayHistory.this, mListImageInfo);
                                    mRecyclerView.setAdapter(mAdapter);
                                    mProgressCircle.setVisibility(View.INVISIBLE);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DisplayHistory.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }
}
