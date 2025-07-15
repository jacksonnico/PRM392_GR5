package com.example.prm392_gr5.Data.model;

public class Notification {
    private int id;
    private String content;
    private String time;
    private int receiverId;
    private String receiverRole;
    private String userName; // Cho Owner xem người gửi
    private boolean isRead; // 👈 Trạng thái đã đọc

    public Notification(int id, String content, String time, int receiverId, String receiverRole) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.receiverId = receiverId;
        this.receiverRole = receiverRole;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getContent() { return content; }
    public String getTime() { return time; }
    public int getReceiverId() { return receiverId; }
    public String getReceiverRole() { return receiverRole; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
