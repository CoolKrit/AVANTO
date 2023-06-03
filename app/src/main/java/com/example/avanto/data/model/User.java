package com.example.avanto.data.model;

public class User {
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userPassword;

    public User(String username, String phonenumber) {
        this.userName = username;
        this.userPhoneNumber = phonenumber;
    }

    public User() {}

    public void setUserName(String username) {
        this.userName = username;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void setUserPhoneNumber(String phonenumber) {
        this.userPhoneNumber = phonenumber;
    }

    public void setUserPassword(String password) {
        this.userPassword = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getPassword() {
        return userPassword;
    }
}
