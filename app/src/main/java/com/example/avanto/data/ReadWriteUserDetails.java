package com.example.avanto.data;

public class ReadWriteUserDetails {
    public String userPhone;

    public ReadWriteUserDetails(String userPhone) {
        this.userPhone = userPhone;
    }

    public ReadWriteUserDetails() {
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
