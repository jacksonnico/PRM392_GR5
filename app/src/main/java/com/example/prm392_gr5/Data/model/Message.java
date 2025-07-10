package com.example.prm392_gr5.Data.model;

public class Message {
    private int id;
    private String displayName;
    private String message;
    private String time;
    private String pitchName;
    private int userId; // ✔✔✔ THêm userId

    public Message(int id, String displayName, String message, String time, String pitchName, int userId) {
        this.id = id;
        this.displayName = displayName;
        this.message = message;
        this.time = time;
        this.pitchName = pitchName;
        this.userId = userId;
    }

    // Constructor cũ cho phù hợp code cũ
    public Message(int id, String displayName, String message, String time, String pitchName) {
        this(id, displayName, message, time, pitchName, 0);
    }

    public int getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getPitchName() { return pitchName; }
    public int getUserId() { return userId; }
}