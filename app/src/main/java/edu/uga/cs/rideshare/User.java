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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return email.equals(user.email);
    }

}
