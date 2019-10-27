package com.project.coen_elec_390;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    private Profile profile;
    private ArrayList<ImageInfo> UrlImages;
    private int doorID;

    // Constructor
    public DatabaseHelper() {
        database = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        database.setFirestoreSettings(settings);

        profiles = new ArrayList<Profile>();
        UrlImages = new ArrayList<ImageInfo>();
    }

    /***Always set doorID before using DatabaseHelper***/
    public void setDoorID(int doorID) {
        this.doorID = doorID;
    }

    public void setProfile(final String username) {
        database.collection("profiles").document("cvu").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d("getProfile", document.getId());
                            if (document.exists()) {
                                profile = new Profile(document.getData().get("username").toString(), document.getData().get("email").toString(),
                                        document.getData().get("password").toString(), Integer.parseInt(document.getData().get("username").toString()));
                            } else {
                                Log.d("getProfile", "No such document");
                            }
                        } else {
                            Log.d("getProfile", "get failed with ", task.getException());
                        }
                    }
                });
    }

    public Profile getProfile() {
        return profile;
    }


    public List<Profile> getProfiles() {
        database.collection("profiles")
                .whereEqualTo("doorID", doorID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                profiles.add(new Profile(document.getData().get("username").toString(), document.getData().get("email").toString(),
                                        document.getData().get("password").toString(), Integer.parseInt(document.getData().get("username").toString())));
                                Log.d("getProfiles", document.getId());
                            }
                        } else {
                            Log.d("getProfiles", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return profiles;
    }

    public List<ImageInfo> getImages() {
        return UrlImages;
    }

    //Store a profile
    public void addProfile(final Profile profile, Uri filePath, final Context context) {
        if (filePath != null) {
            storageReference = FirebaseStorage.getInstance().getReference("door_" + doorID + "/profiles");
            StorageReference ref = storageReference.child(profile.getUsername());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            HashMap<String, Object> user = new HashMap<>();
                            user.put("username", profile.getUsername());
                            user.put("email", profile.getEmail());
                            user.put("password", profile.getPassword());
                            user.put("doorId", doorID);

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
                            database.collection("images").document("door_" + doorID)
                                    .set(image)
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

    public void setUrlImage(String iD) {
        storageReference = FirebaseStorage.getInstance().getReference("door_" + doorID);
        storageReference.child(iD).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("URL", "onSuccess: uri = " + uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }
}
