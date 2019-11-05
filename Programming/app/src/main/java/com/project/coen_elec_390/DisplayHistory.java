package com.project.coen_elec_390;

import android.content.SharedPreferences;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DisplayHistory extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private SharedPreferences mSharedPreference;
    private DatabaseHelper mDatabaseHelper;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    private List<ImageInfo> mListImageInfo;
    private int mDoorID;

    private final String TAG = "HISTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_history);
        setTitle("History");

        mDatabaseHelper = new DatabaseHelper();

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DisplayHistory.this));
        mRecyclerView.setHasFixedSize(true);

        mSharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        mDoorID = mSharedPreference.getInt("doorID", 0);
        mListImageInfo = new ArrayList<>();

        mAdapter = new ImageAdapter(DisplayHistory.this, mListImageInfo);
        mRecyclerView.setAdapter(mAdapter);

        storage = FirebaseStorage.getInstance();
        storageReference = mDatabaseHelper.getStorageReference("door_" + mDoorID + "/history");

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult result) {
                for(StorageReference fileRef : result.getItems()) {
                    StorageReference gsReference = storage.getReferenceFromUrl(fileRef.toString());
                    gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mListImageInfo.add(new ImageInfo(getFileName(uri.toString())
                                    , uri.toString()
                                    , Integer.toString(mDoorID)));
                            Log.d(TAG, uri.toString());

                            mAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
            }
        });
    }

    private String getFileName(String url) {
        int counter = 0;
        String name = "";
        for (int i = 0; i < url.length(); ++i) {
            if (url.charAt(i) == '%' && counter == 0) {
                ++counter;
            } else if (url.charAt(i) == '%' && counter == 1) {
                i += 3;
                while (url.charAt(i) != '?') {
                    name += url.charAt(i);
                    ++i;
                }
                Log.d(TAG, name);
                return name;
            }
        }
        return name;
    }
}
