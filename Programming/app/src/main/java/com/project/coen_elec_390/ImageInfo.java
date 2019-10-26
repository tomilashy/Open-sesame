package com.project.coen_elec_390;

public class ImageInfo {
    String url;
    int doorID;

    public ImageInfo() {
    }

    public ImageInfo(String url, int doorID) {
        this.url = url;
        this.doorID = doorID;
    }

    public String getUrl() {
        return url;
    }

    public int getDoorID() {
        return doorID;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDoorID(int doorID) {
        this.doorID = doorID;
    }
}


