package com.project.coen_elec_390;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DisplayHistory extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private DatabaseReference mDatabaseRef;
    private SharedPreferences mSharedPreference;
    private List<ImageInfo> listImageInfo;
    private int mDoorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_history);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayHistory.this));
        mRecyclerView.setHasFixedSize(true);

        mProgressCircle = findViewById(R.id.progress_circle);

        mSharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        mDoorID = mSharedPreference.getInt("doorID", 0);
        listImageInfo = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("door_" + mDoorID + "/history");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ImageInfo imageInfo = postSnapshot.getValue(ImageInfo.class);
                    listImageInfo.add(imageInfo);
                }

                mAdapter = new ImageAdapter(DisplayHistory.this, listImageInfo);

                mRecyclerView.setAdapter(mAdapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DisplayHistory.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }
}
