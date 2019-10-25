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
    private List<Profile> profileAdmins;
    public Map <String, Boolean> profiles = new HashMap<>();

    // Constructors
    public Profile() {}

    public Profile(String uName, String pWord, String eMail) {
        username = uName;
        password = pWord;
        email = eMail;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("password", password);
        result.put("email", email);
        result.put("profileAdmins", profileAdmins);
        return result;
    }

    // Getters
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
    public List<Profile> getAdmins() {return profileAdmins;}

    // Setters
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setEmail(String email) {this.email = email;}

    public void addAdmin(Profile profile){
        profileAdmins.add(profile);
    }
}