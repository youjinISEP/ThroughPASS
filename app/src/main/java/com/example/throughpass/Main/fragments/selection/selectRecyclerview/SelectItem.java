package com.example.throughpass.Main.fragments.selection.selectRecyclerview;

import android.graphics.drawable.Drawable;

public class SelectItem {

    private Drawable ride_Image;
    private String ride_Name;
    private String restTime;

    public SelectItem(Drawable ride_Image, String ride_Name, String restTime) {
        this.ride_Image = ride_Image;
        this.ride_Name = ride_Name;
        this.restTime = restTime;
    }

    public Drawable getRide_Image() {
        return ride_Image;
    }

    public void setRide_Image(Drawable ride_Image) {
        this.ride_Image = ride_Image;
    }

    public String getRide_Name() {
        return ride_Name;
    }

    public void setRide_Name(String ride_Name) {
        this.ride_Name = ride_Name;
    }

    public String getRestTime() {
        return restTime;
    }

    public void setRestTime(String restTime) {
        this.restTime = restTime;
    }
}
