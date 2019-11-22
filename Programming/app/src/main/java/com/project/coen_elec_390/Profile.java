package com.project.coen_elec_390;

public class Profile {
    // Data Members
    private String username;
    private String password;
    private String phoneNum;
    private String imageUrl;
    private int doorID;

    // Constructors
    public Profile() {}

    public Profile(String username, String phone, String password, int doorID, String imageUrl) {
        this.username = username;
        this.phoneNum = phone;
        this.password = password;
        this.doorID = doorID;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getPhoneNumber() {return phoneNum;}
    public String getImageUrl() {return imageUrl;}
    public int getDoorID() {return doorID;}

    // Setters
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setPhoneNumber(String phone) {this.phoneNum = phone;}
    public void setDoorID(int doorID) {this.doorID = doorID;}
}