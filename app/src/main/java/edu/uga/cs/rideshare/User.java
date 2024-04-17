package edu.uga.cs.rideshare;

public class User {
    public String email;
    public String pass;
    public int points;

    public User() {

    }

    public User(String email, String pass) {
        this.email = email;
        this.pass = pass;
        this.points = 50;
    }

    public void addPoints() {
        points += 50;
    }

    public void subtractPoints() {
        points -= 50;
    }

}
