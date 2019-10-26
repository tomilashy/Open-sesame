package com.project.coen_elec_390;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile {
    // Data Members
    private String username;
    private String password;
    private String email;
    private int doorID;
    private List<Profile> admins;

    // Constructors
    public Profile() {}

    public Profile(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("password", password);
        result.put("email", email);
        result.put("doorId", doorID);
        return result;
    }

    // Getters
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
    public int getDoorID() {return doorID;}
    public List<Profile> getAdmins() {return admins;}

    // Setters
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setEmail(String email) {this.email = email;}
    public void setDoorID(int doorID) {this.doorID = doorID;}
    public void addAdmin(Profile profile){
        admins.add(profile);
    }
}