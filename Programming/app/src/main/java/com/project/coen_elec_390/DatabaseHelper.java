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

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {
    private FirebaseFirestore database;
    private StorageReference storageReference;
    private int doorID;

    public DatabaseHelper() {
        database = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        database.setFirestoreSettings(settings);
    }

    /***Always set doorID before using DatabaseHelper***/
    public void setDoorID(int doorID) {
        this.doorID = doorID;
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }

    //Store a profile
    public void addProfile(final Profile profile, Uri filePath) {
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
                            user.put("doorID", doorID);

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
