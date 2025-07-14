package com.example.prm392_gr5.Data.model;

public class Message {
    private int id;
    private String sender;
    private String message;
    private String time;
    private String pitchName;
    private int userId;

    public Message(int id, String sender, String message, String time, String pitchName, int userId) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.pitchName = pitchName;
        this.userId = userId;
    }

    // ✅ Getter và Setter cho id
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // ✅ Getter và Setter cho sender
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    // ✅ Getter và Setter cho message
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // ✅ Getter và Setter cho time
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    // ✅ Getter và Setter cho pitchName
    public String getPitchName() { return pitchName; }
    public void setPitchName(String pitchName) { this.pitchName = pitchName; }

    // ✅ Getter và Setter cho userId
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
