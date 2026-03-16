package com.example.pgconnect;

public class BookingRequestModel {

    private String bookingId;
    private String userName;
    private String userPhone;
    private String pgName;
    private String pgLocation;
    private String idProofUrl;
    private String status;

    public BookingRequestModel() {
        // required for firebase
    }

    public BookingRequestModel(String bookingId, String userName, String userPhone,
                               String pgName, String pgLocation, String idProofUrl, String status) {
        this.bookingId = bookingId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.pgName = pgName;
        this.pgLocation = pgLocation;
        this.idProofUrl = idProofUrl;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getPgName() {
        return pgName;
    }

    public String getPgLocation() {
        return pgLocation;
    }

    public String getIdProofUrl() {
        return idProofUrl;
    }

    public String getStatus() {
        return status;
    }
}
