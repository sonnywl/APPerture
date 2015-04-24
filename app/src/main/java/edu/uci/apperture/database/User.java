package edu.uci.apperture.database;

/**
 * Object for representing user id and username
 * Created by Sonny on 4/16/2015.
 */
public class User {

    private final String username;
    private final int userId;

    public User(int user_id, String username) {
        this.userId = user_id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }
}
