package com.example.wabbleapp;

public class EncoderEmail {
    public static String EncodeString(String Email) {
        return Email.replace(".", ",");
    }

    public static String DecodeString(String Email) {
        return Email.replace(",", ".");
    }
}
