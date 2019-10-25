package com.project.coen_elec_390;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {
    private DatabaseReference database;

    // Constructor
    public DatabaseHelper() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    // Set up a new Profile
    public void writeNewProfile (String username, String password, String email) {
        Profile profile = new Profile(username, password, email);

        database.child("profiles").child(username).setValue(profile);
    }

    public void updateProfile (String username, String password, String email) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = database.child("profiles").push().getKey();
        Profile profile = new Profile(username, password, email);
        Map<String, Object> profileValues = profile.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/profiles/" + key, profileValues);
        childUpdates.put("/admins/" + key, profileValues);

        database.updateChildren(childUpdates);
    }
}
