package com.project.coen_elec_390;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private DatabaseReference database;
    private List<Profile> profiles;

    // Constructor
    public DatabaseHelper() {
        database = FirebaseDatabase.getInstance().getReference();
        ValueEventListener eventListener = new ValueEventListener() {
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
        database.addValueEventListener(eventListener);
    }

    public List<Profile> getProfiles()
    {
        return profiles;
    }

    // Set up a new Profile
    public void addProfile(String username, String password, String email, int doorID) {
        Profile profile = new Profile(username, password, email);
        database.child("profiles").child(username).setValue(profile);
    }
}
