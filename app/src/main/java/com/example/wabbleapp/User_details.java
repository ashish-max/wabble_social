package com.example.wabbleapp;

public class User_details {
    private String name, email, mobileNumber;

    public User_details() {  // Empty constructor

    }

    public String getFullname() {
        return name;
    }

    public void setFullname(String name) {
        this.name = name;
    }

    public String getEmail() {
        return EncoderEmail.DecodeString(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return mobileNumber;
    }

    public void setPhoneNo(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}

