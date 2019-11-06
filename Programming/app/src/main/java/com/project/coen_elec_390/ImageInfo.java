package com.project.coen_elec_390;

import android.util.Log;

public class ImageInfo {
    private String imageName;
    private String imageUrl;

    final String TAG = "IMAGEINFO";

    public ImageInfo(String name, String url) {
        imageName = name;
        imageUrl = url;
    }

    public int parseNameToMinutes() {
        String parsed = "";
        int minutes = 0;
        int hours = 0;
        int days = 0;
        int months = 0;
        int years = 0;
        int counter = 0;
        for (int i = 0; i < imageName.length(); ++i) {
            if (imageName.charAt(i) == '.') {
                if (counter == 0) {
                    ++counter;
                    hours = Integer.parseInt(parsed);
                    parsed = "";
                } else if (counter == 1) {
                    ++counter;
                    minutes = Integer.parseInt(parsed);
                    parsed = "";
                } else if (counter == 2) {
                    ++counter;
                    days = Integer.parseInt(parsed);
                    parsed = "";
                } else if (counter == 3) {
                    ++counter;
                    months = Integer.parseInt(parsed);
                    parsed = "";
                } else {
                    years = Integer.parseInt(parsed);
                    parsed = "";
                }
            } else {
                parsed += imageName.charAt(i);
            }
        }

        Log.d(TAG, "hours: " + hours + ", minutes: " + minutes + ", " +
                "days: " + days + ", months: " + months + ", years: " + years);
        return minutes + hours * 60 + days * 1440 + months * 43200 + years * 518400;
    }

    public String getImageName() { return imageName; }
    public String getImageUrl() {
        return imageUrl;
    }
}