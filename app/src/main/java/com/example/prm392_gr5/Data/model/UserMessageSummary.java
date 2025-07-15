package com.example.prm392_gr5.Data.model;

public class UserMessageSummary {
    private int userId;
    private String userName;
    private String lastMessage;
    private String lastTime;
    private String pitchName;
    private String phoneNumber;

    // ✅ Constructor đầy đủ
    public UserMessageSummary(int userId, String userName, String lastMessage, String lastTime, String pitchName, String phoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
        this.pitchName = pitchName;
        this.phoneNumber = phoneNumber;
    }

    // ✅ Getters
    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastTime() {
        return lastTime;
    }

    public String getPitchName() {
        return pitchName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // ✅ Optional: Setters if needed
}
