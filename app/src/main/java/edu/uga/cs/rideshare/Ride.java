package edu.uga.cs.rideshare;

import com.google.firebase.database.Exclude;

import java.util.Date;

public class Ride {

    public String key;
    public String date;
    public String goingTo;
    public String from;
    public boolean driverAccepted;
    public boolean riderAccepted;
    public User driver;
    public User rider;
    public boolean rideCompletedDriver;
    public boolean rideCompletedRider;

    public Ride(){

    }

    public Ride (String key, String date, String goingTo, String from, User driver, User rider) {
        this.key = key;
        this.date = date;
        this.goingTo = goingTo;
        this.from = from;

        if (driver != null && rider != null) {
            riderAccepted = true;
            driverAccepted = true;
            this.driver = driver;
            this.rider = rider;
        } else if (driver == null && rider != null) {
            driverAccepted = false;
            riderAccepted = true;
            this.rider = rider;
        } else if (rider == null && driver != null){
            riderAccepted = false;
            driverAccepted = true;
            this.driver = driver;
        } else {
            riderAccepted = false;
            driverAccepted = false;
        }
    }

    // Method to set the key
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    // Method to get the key
    @Exclude
    public String getKey() {
        return key;
    }

    public void updatedCompletedDriver(boolean val){
        rideCompletedDriver = val;
    }
    public void updatedCompletedRider(boolean val){
        rideCompletedRider = val;
    }

}
