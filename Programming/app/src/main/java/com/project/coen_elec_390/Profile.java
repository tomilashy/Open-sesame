package com.project.coen_elec_390;

public class Profile {
    // Data Members
    private String username;
    private String password;
    private String email;

    // Constructor
    public Profile(String uName, String pWord, String eMail) {
        username = uName;
        password = pWord;
        email = eMail;
    }

    // Getters
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}

    // Setters
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setEmail(String email) {this.email = email;}
}