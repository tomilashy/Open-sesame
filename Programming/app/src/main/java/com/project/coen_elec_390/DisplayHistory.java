package com.project.coen_elec_390;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DisplayHistory extends AppCompatActivity {

    private ImageAdapter adapter;
    private RecyclerView recyclerView;

    private SharedPreferences sharedPreference;
    private DatabaseHelper databaseHelper;
    private StorageReference storageReference;

    private Handler handler;

    private ArrayList<ImageInfo> imageInfoList;
    private int doorID;

    private final String TAG = "HISTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_history);
        setTitle("History");

        databaseHelper = new DatabaseHelper();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(DisplayHistory.this));
        recyclerView.setHasFixedSize(true);

        //Add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        doorID = sharedPreference.getInt("doorID", 0);
        imageInfoList = new ArrayList<>();

        handler = new Handler();

        adapter = new ImageAdapter(DisplayHistory.this, imageInfoList);
        recyclerView.setAdapter(adapter);

        storageReference = databaseHelper.getStorageReference("door_" + doorID + "/history");

        getPicturesFromStorage();
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

    private void getPicturesFromStorage() {
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult result) {
                for (StorageReference fileRef : result.getItems()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String rawDate = getFileName(uri.toString());
                            imageInfoList.add(new ImageInfo(getDate(rawDate), rawDate, uri.toString()));
                            Log.d(TAG, uri.toString());

                            Collections.sort(imageInfoList, new Comparator<ImageInfo>() {
                                @Override
                                public int compare(ImageInfo a, ImageInfo b)
                                {
                                    return Long.compare(b.getDateInSeconds(), a.getDateInSeconds());
                                }
                            });

                            adapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(TAG, "Unable to get uri!");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d(TAG, "listALL() failed!");
            }
        });

        handler.postDelayed(networkCheck, 0);
    }

    Runnable networkCheck = new Runnable() {
        @Override
        public void run() {

            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(DisplayHistory.this.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                    startActivity(new Intent(DisplayHistory.this, LoginActivity.class));
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

    private String getDate(String input) {
        int counter = 0;
        String date = "";
        String temp = "";
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == '.' || i == input.length()) {
                if (counter == 0) {
                    date += temp + " hours ";
                    temp = "";
                    ++counter;
                } else if (counter == 1) {
                    date += temp + " minutes ";
                    temp = "";
                    ++counter;
                }  else if (counter == 2) {
                    temp = "";
                    ++counter;
                } else if (counter == 3) {
                    date += temp + "/";
                    temp = "";
                    ++counter;
                } else if (counter == 4) {
                    date += temp + "/";
                    temp = "";
                    ++counter;
                } else if (counter == 5) {
                    date += temp;
                }
            } else {
                temp += input.charAt(i);
            }
        }
        return date;
    }
}
