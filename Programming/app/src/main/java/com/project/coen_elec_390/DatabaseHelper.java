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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseHelper {
    private FirebaseFirestore database;
    private StorageReference storageReference;
    private ArrayList<Profile> profiles;
    private ArrayList<ImageInfo> images;

    // Constructor
    public DatabaseHelper() {
        database = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("door1");

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        database.setFirestoreSettings(settings);

        profiles = new ArrayList<Profile>();
        images = new ArrayList<ImageInfo>();
    }

    public List<Profile> getProfiles()
    {
        return profiles;
    }

    //Store a profile
    public void addProfileImage(final Profile profile, Uri filePath, final Context context) {
        if(filePath != null)
        {
            final String url = UUID.randomUUID().toString() + ".png";
            StorageReference ref = storageReference.child(url);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", profile.getUsername());
                            user.put("email", profile.getEmail());
                            user.put("password", profile.getPassword());
                            user.put("urlImage", url);
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
                                            Toast.makeText(context, "Failed upload of profile!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

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

    //Store a picture for history
    public void addHistoryImage(final String iD,  Uri filePath,  final Context context) {
        if (filePath != null) {
            StorageReference ref = storageReference.child(UUID.randomUUID().toString() + ".png");
            final String url = filePath.toString();
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Map<String, Object> image = new HashMap<>();
                            image.put("iD", iD);
                            image.put("url", url);
                            database.collection("profiles").document(iD)
                                    .set(images)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Failed upload of profile!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
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
