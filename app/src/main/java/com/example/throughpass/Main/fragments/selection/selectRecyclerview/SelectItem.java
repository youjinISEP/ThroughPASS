package com.example.throughpass.Main.fragments.selection.selectRecyclerview;

public class SelectItem {

    private String ride_Image;
    private String ride_Name;
    private String restTime;
    private Integer ride_Code;
    private Integer reservationOrder;

    public SelectItem(Integer ride_Code, String ride_Name, String ride_Image, String restTime, Integer reservationOrder) {
        this.ride_Code = ride_Code;
        this.ride_Name = ride_Name;
        this.ride_Image = ride_Image;
        this.restTime = restTime;
        this.reservationOrder = reservationOrder;
    }
    public String getRide_Image() {
        return ride_Image;
    }

    public void setRide_Image(String ride_Image) {
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

    public Integer getRide_Code(){ return ride_Code;}

    public void setRide_Code(Integer ride_Code){ this.ride_Code = ride_Code;}

    public Integer getReservationOrder(){ return reservationOrder;}

    public void setReservationOrder(Integer reservationOrder){ this.reservationOrder = reservationOrder;}
}
