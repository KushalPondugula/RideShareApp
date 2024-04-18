package edu.uga.cs.rideshare;

import java.util.Date;

public class Ride {
    public Date date;
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

    public Ride (Date date, String goingTo, String from, User driver, User rider) {
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

    public void updatedCompletedDriver(boolean val){
        rideCompletedDriver = val;
    }
    public void updatedCompletedRider(boolean val){
        rideCompletedRider = val;
    }

}
