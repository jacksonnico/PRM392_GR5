package com.example.prm392_gr5.Data.model;

public class UserMessageSummary {
    private String displayName;
    private String lastMessage;
    private String lastTime;
    private String pitchName;
    private String phoneNumber;

    public UserMessageSummary(String displayName, String lastMessage, String lastTime, String pitchName, String phoneNumber) {
        this.displayName = displayName;
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
        this.pitchName = pitchName;
        this.phoneNumber = phoneNumber;
    }

    public String getDisplayName() { return displayName; }
    public String getLastMessage() { return lastMessage; }
    public String getLastTime() { return lastTime; }
    public String getPitchName() { return pitchName; }
    public String getPhoneNumber() { return phoneNumber; }
}
