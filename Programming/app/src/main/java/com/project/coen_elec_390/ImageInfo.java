package com.project.coen_elec_390;

import android.util.Log;

public class ImageInfo {
    
    private String imageName;
    private String imageUrl;
    private long dateInSeconds;

    final String TAG = "IMAGEINFO";

    public ImageInfo(String name, String url) {
        imageName = name;
        imageUrl = url;

        parseNameToMinutes();
    }

    public void parseNameToMinutes() {
        String parsed = "";
        long seconds = -1;
        long minutes = -1;
        long hours = -1;
        long days = -1;
        long months = -1;
        long years = -1;
        for (int i = 0; i < imageName.length(); ++i) {
            if (imageName.charAt(i) == '.') {
                if (hours == -1) {
                    hours = Long.parseLong(parsed);
                    parsed = "";
                } else if (minutes == -1) {
                    minutes = Long.parseLong(parsed);
                    parsed = "";
                } else if (seconds == -1) {
                    seconds = Long.parseLong(parsed);
                    parsed = "";
                } else if (days == -1) {
                    days = Long.parseLong(parsed);
                    parsed = "";
                } else if (months == -1) {
                    months = Long.parseLong(parsed);
                    parsed = "";
                } else {
                    years = Long.parseLong(parsed);
                    parsed = "";
                }
            } else {
                parsed += imageName.charAt(i);
            }
        }

        Log.d(TAG, "hours: " + hours + ", minutes: " + minutes + ", " +
                "days: " + days + ", months: " + months + ", years: " + years);
        dateInSeconds = seconds + minutes * 60 + hours * 3600 + days * 86400 + months * 2592000 + years * 31557600 ;
    }

    public String getImageName() { return imageName; }
    public String getImageUrl() { return imageUrl; }
    public long getDateInSeconds () { return dateInSeconds; }
}