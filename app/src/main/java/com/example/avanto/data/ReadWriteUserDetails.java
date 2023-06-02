package com.example.avanto.data;

public class ReadWriteUserDetails {
    public String userName, userPhone;

    public ReadWriteUserDetails(String userName, String userPhone) {
        this.userName = userName;
        this.userPhone = userPhone;
    }

    public ReadWriteUserDetails() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
