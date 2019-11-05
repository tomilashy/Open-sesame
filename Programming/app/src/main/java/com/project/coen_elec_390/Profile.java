package com.project.coen_elec_390;

import java.util.ArrayList;
import java.util.List;


public class Profile {
    // Data Members
    private String username;
    private String password;
    private String email;
    private String imageUrl;
    private int doorID;
    private ArrayList<Profile> admins;

    // Constructors
    public Profile() {}

    public Profile(String username, String email, String password, int doorID) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.doorID = doorID;

        admins = new ArrayList<Profile>();
    }

    // Getters
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
    public String getImageUrl() {return imageUrl;}
    public int getDoorID() {return doorID;}
    public List<Profile> getAdmins() {return admins;}

    // Setters
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setEmail(String email) {this.email = email;}
    public void setImageUrl(String url) {this.imageUrl = url;}
    public void setDoorID(int doorID) {this.doorID = doorID;}
    public void addAdmin(Profile profile){
        admins.add(profile);
    }
}