package com.project.coen_elec_390;

public class ImageInfo {
    String url;
    String iD;

    public ImageInfo() {}

    public ImageInfo(String iD) {
        this.iD = iD;
    }

    public String getUrl() {
        return url;
    }

    public String getID() {
        return iD;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDoorID(String iD) {
        this.iD = iD;
    }
}


