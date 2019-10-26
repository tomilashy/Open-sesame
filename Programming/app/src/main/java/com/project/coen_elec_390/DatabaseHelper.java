package com.project.coen_elec_390;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.UUID;

public class DatabaseHelper {
    private DatabaseReference database;
    private StorageReference storageReference;
    private List<Profile> profiles;
    private List<ImageInfo> images;

    // Constructor
    public DatabaseHelper() {
        database = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("door1");

        //Get profiles from database
        ValueEventListener profilesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile profile = ds.child("profiles").getValue(Profile.class);
                    profiles.add(profile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Get images for history from database
        ValueEventListener imagesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ImageInfo imageUploadInfo = ds.child("profiles").getValue(ImageInfo.class);
                    images.add(imageUploadInfo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.addValueEventListener(profilesEventListener);
        database.addValueEventListener(imagesEventListener);
    }

    public List<Profile> getProfiles()
    {
        return profiles;
    }

    // Store a profile
    public void addProfile(String username, String password, String email, String urlImage, int doorID) {
        Profile profile = new Profile(username, password, email, urlImage, doorID);
        database.child("profiles").child(username).setValue(profile);
    }

    //Store a picture for a profile
    private void addProfileImage(final String username, final String password, final String email, final int doorID, Uri filePath, final Context context) {
        if(filePath != null)
        {
            StorageReference ref = storageReference.child(UUID.randomUUID().toString());
            final String url = filePath.toString();
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Profile profile = new Profile(username, password, email, url, doorID);
                            database.child("profiles").child(username).setValue(profile);
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //Store a picture for a profile
    private void addHistoryImage(final int doorID, Uri filePath,  final Context context) {
        if (filePath != null) {
            StorageReference ref = storageReference.child(UUID.randomUUID().toString());
            final String url = filePath.toString();
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ImageInfo imageInfo = new ImageInfo(url, doorID);
                            // Getting image upload ID.
                            String ImageUploadId = database.push().getKey();
                            // Adding image upload id s child element into databaseReference.
                            database.child(ImageUploadId).setValue(imageInfo);
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
