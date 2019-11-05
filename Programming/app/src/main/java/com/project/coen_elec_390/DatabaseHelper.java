package com.project.coen_elec_390;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DatabaseHelper {
    private FirebaseFirestore database;

    public DatabaseHelper() {
        database = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        database.setFirestoreSettings(settings);
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }

    public StorageReference getStorageReference(final String reference) {return FirebaseStorage.getInstance().getReference(reference);}
}
