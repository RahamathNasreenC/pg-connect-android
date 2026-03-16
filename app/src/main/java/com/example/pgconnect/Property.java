package com.example.pgconnect;

public class Property {

    private int id;
    private String title, location, roomType, amenities, imagePath;

    private String description;
    private int availableRooms;
    private String contactNumber;

    private String bedImagePath;   // NEW

    private int rent;

    public Property(int id, String title, String location, String roomType,
                    int rent, String amenities, String imagePath,
                    String description, int availableRooms, String contactNumber,
                    String bedImagePath) {

        this.id = id;
        this.title = title;
        this.location = location;
        this.roomType = roomType;
        this.rent = rent;
        this.amenities = amenities;
        this.imagePath = imagePath;

        this.description = description;
        this.availableRooms = availableRooms;
        this.contactNumber = contactNumber;

        this.bedImagePath = bedImagePath;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getRent() {
        return rent;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getBedImagePath() {
        return bedImagePath;
    }
}
