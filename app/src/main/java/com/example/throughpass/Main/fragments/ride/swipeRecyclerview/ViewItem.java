package com.example.throughpass.Main.fragments.ride.swipeRecyclerview;

import android.graphics.drawable.Drawable;

public class ViewItem {
    private int status;
    private Drawable ride_Image;
    private String ride_Name;
    private String restTime;

    private int attr_code;
    private String name;
    private int personnel;
    private int run_time;
    private String start_time;
    private String end_time;
    private String location;
    private String coordinate;
    private String img_url;
    private String info;
    private int wait_minute;

    private int waitStatus;
    private int resvStatus;

    public ViewItem(Drawable ride_Image, String ride_Name, String restTime){
        this.ride_Image = ride_Image;
        this.ride_Name = ride_Name;
        this.restTime = restTime;
    }

    public int getWaitStatus() {
        return waitStatus;
    }

    public void setWaitStatus(int waitStatus) {
        this.waitStatus = waitStatus;
    }

    public int getResvStatus(){
        return resvStatus;
    }

    public void setResvStatus(int resvStatus) {
        this.resvStatus = resvStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getAttr_code() {
        return attr_code;
    }

    public void setAttr_code(int attr_code) {
        this.attr_code = attr_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPersonnel() {
        return personnel;
    }

    public void setPersonnel(int personnel) {
        this.personnel = personnel;
    }

    public int getRun_time() {
        return run_time;
    }

    public void setRun_time(int run_time) {
        this.run_time = run_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getWait_minute() {
        return wait_minute;
    }

    public void setWait_minute(int wait_minute) {
        this.wait_minute = wait_minute;
    }
}
