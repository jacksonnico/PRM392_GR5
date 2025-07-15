package com.example.prm392_gr5.Data.model;

public class Message {
    private int id;
    private String sender;
    private String message;
    private String time;
    private String pitchName;
    private int userId;
    private int ownerId; // Thêm ownerId

    public Message(int id, String sender, String message, String time, String pitchName, int userId, int ownerId) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.pitchName = pitchName;
        this.userId = userId;
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getPitchName() {
        return pitchName;
    }

    public int getUserId() {
        return userId;
    }

    public int getOwnerId() {
        return ownerId;
    }
}