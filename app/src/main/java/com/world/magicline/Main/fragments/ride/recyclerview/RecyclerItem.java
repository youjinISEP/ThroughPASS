package com.world.magicline.Main.fragments.ride.recyclerview;
import android.graphics.drawable.Drawable;

public class RecyclerItem {
    private Drawable rideImage;
    private String rideName;
    private String rideRestTime;
    private String rideLocation;



    public Drawable getRideImage(){
        return this.rideImage;
    }

    public void setRideImage(Drawable rideImage){
        this.rideImage = rideImage;
    }

    public String getRideName() {
        return rideName;
    }

    public void setRideName(String rideName) {
        this.rideName = rideName;
    }

    public String getRideRestTime() {
        return rideRestTime;
    }

    public void setRideRestTime(String rideRestTime) {
        this.rideRestTime = rideRestTime;
    }

    public String getRideLocation() {
        return rideLocation;
    }

    public void setRideLocation(String rideLocation) {
        this.rideLocation = rideLocation;
    }
}
