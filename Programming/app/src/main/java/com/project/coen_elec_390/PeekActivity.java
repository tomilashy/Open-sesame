package com.project.coen_elec_390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PeekActivity extends AppCompatActivity {

    private ImageView peekImage;

    private SharedPreferences sharedPreference;
    private DatabaseHelper databaseHelper;
    private StorageReference storageReference;

    private ArrayList<ImageInfo> imageInfoList;
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
        imageInfoList = new ArrayList<>();

        storageReference = databaseHelper.getStorageReference("door_" + doorID + "/history");

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult result) {
                for (StorageReference fileRef : result.getItems()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageInfoList.add(new ImageInfo(getFileName(uri.toString())
                                    , uri.toString()));
                            Log.d(TAG, uri.toString());

                            Collections.sort(imageInfoList, new Comparator<ImageInfo>() {
                                @Override
                                public int compare(ImageInfo a, ImageInfo b)
                                {
                                    return Long.compare(b.getDateInSeconds(), a.getDateInSeconds());
                                }
                            });

                            Picasso.with(PeekActivity.this)
                                    .load(imageInfoList.get(0).getImageUrl())
                                    .fit()
                                    .into(peekImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) { }
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
        int startIndex = - 1;
        int endIndex;
        String name = "";
        for (int i = 0; i < url.length(); ++i) {
            if (url.charAt(i) == '%' && counter == 0) {
                ++counter;
            } else if (url.charAt(i) == '%' && counter == 1) {
                i += 3;
                startIndex = i;
            } else if (url.charAt(i) == '?') {
                endIndex = i;
                name = url.substring(startIndex, endIndex);
                Log.d(TAG, name);
                return name;
            }
        }
        return name;
    }
}
