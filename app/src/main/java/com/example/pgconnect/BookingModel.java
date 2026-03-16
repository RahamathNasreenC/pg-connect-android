package com.example.pgconnect;

public class BookingModel {

    int bookingId;
    String title, location, username, phone, idProofPath, status;
    int rent;

    public BookingModel(int bookingId, String title, String location, int rent,
                        String username, String phone, String idProofPath, String status) {
        this.bookingId = bookingId;
        this.title = title;
        this.location = location;
        this.rent = rent;
        this.username = username;
        this.phone = phone;
        this.idProofPath = idProofPath;
        this.status = status;
    }
}
