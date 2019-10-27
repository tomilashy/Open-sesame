package com.project.coen_elec_390;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private FirebaseFirestore database;
    private StorageReference storageReference;
    private ArrayList<Profile> profiles;
    private ArrayList<ImageInfo> images;
    private int doorID;

    // Constructor
    public DatabaseHelper() {
        database = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        database.setFirestoreSettings(settings);

        profiles = new ArrayList<Profile>();
        images = new ArrayList<ImageInfo>();
    }

    public void setDoorID(int doorID) {
        this.doorID = doorID;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    //Store a profile
    public void addProfile(final Profile profile, Uri filePath, final Context context) {
        if (filePath != null) {
            storageReference = FirebaseStorage.getInstance().getReference("door_" + doorID);
            StorageReference ref = storageReference.child(profile.getUsername());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", profile.getUsername());
                            user.put("email", profile.getEmail());
                            user.put("password", profile.getPassword());
                            user.put("doorID", profile.getDoorID());

                            database.collection("profiles").document(profile.getUsername())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }

    //Store a picture for history
    public void addHistoryImage(Uri filePath, final Context context) {
        if (filePath != null) {
            final String iD = "door_" + doorID;
            storageReference = FirebaseStorage.getInstance().getReference(iD);
            StorageReference ref = storageReference.child("door_" + doorID);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Map<String, Object> image = new HashMap<>();
                            image.put("iD", iD);
                            image.put("url", "to_be_implemented");
                            database.collection("images").document(iD)
                                    .set(images)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }

    public void getImageURL(String iD) {
        storageReference = FirebaseStorage.getInstance().getReference("door_" + doorID);
        storageReference.child(iD).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Log.d("URL", "onSuccess: uri = " + uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }
}
