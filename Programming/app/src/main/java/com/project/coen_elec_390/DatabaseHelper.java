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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseHelper {
    private DatabaseReference database;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private List<Profile> profiles;
    private List<ImageInfo> images;

    // Constructor
    public DatabaseHelper() {
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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

    //Store a picture
    private void addImage(Uri filePath, final Context context) {
        if(filePath != null)
        {
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
