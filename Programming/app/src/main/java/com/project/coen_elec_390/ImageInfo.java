package com.project.coen_elec_390;

public class ImageInfo {
    private String imageName;
    private String imageUrl;
    private String  doorID;

    public ImageInfo() {
        // empty constructor needed
    }

    public ImageInfo(String name, String url, String id) {
        imageName = name;
        imageUrl = url;
        doorID = id;
    }

    // Getters
    public String getImageName() { return imageName; }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getImageID() {
        return doorID;
    }

    // Setters
    public void setImageName(String name) {
        this.imageName = name;
    }
    public void setImageUrl(String url) {
        this.imageUrl = url;
    }
    public void setImageDoorID(String iD) {
        this.doorID = iD;
    }
}