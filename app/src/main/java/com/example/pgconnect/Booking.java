package com.example.pgconnect;

public class Booking {

    int id, propertyId, rent;
    String pgName, location, roomType, userEmail, mobile, idProofPath, status;

    public Booking(int id, int propertyId, String pgName, String location, int rent,
                   String roomType, String userEmail, String mobile, String idProofPath, String status) {

        this.id = id;
        this.propertyId = propertyId;
        this.pgName = pgName;
        this.location = location;
        this.rent = rent;
        this.roomType = roomType;
        this.userEmail = userEmail;
        this.mobile = mobile;
        this.idProofPath = idProofPath;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public String getPgName() {
        return pgName;
    }

    public String getLocation() {
        return location;
    }

    public int getRent() {
        return rent;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getMobile() {
        return mobile;
    }

    public String getIdProofPath() {
        return idProofPath;
    }

    public String getStatus() {
        return status;
    }
}
