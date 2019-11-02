package com.project.coen_elec_390;

import android.content.Context;
import android.net.Uri;

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

    public void setDoorID(int doorID) {
        this.doorID = doorID;
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }

    public StorageReference getStorageReference(final String reference) {return FirebaseStorage.getInstance().getReference(reference);}
}
