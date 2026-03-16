package com.example.pgconnect;

public class NotificationItem {

    private int id;
    private String userEmail;
    private String message;
    private int isRead; // 0 = unread, 1 = read
    private String createdAt;

    public NotificationItem(int id, String userEmail, String message, int isRead, String createdAt) {
        this.id = id;
        this.userEmail = userEmail;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getMessage() { return message; }
    public int getIsRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setMessage(String message) { this.message = message; }
    public void setIsRead(int isRead) { this.isRead = isRead; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
