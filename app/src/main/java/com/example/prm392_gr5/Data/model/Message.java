package com.example.prm392_gr5.Data.model;

public class Message {
    private int id;
    private String sender;
    private String message;
    private String time;
    private String receiverType;

    public Message(int id, String sender, String message, String time, String receiverType) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.receiverType = receiverType;
    }

    public int getId() { return id; }
    public String getSender() { return sender; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getReceiverType() { return receiverType; }

    public void setId(int id) { this.id = id; }
    public void setSender(String sender) { this.sender = sender; }
    public void setMessage(String message) { this.message = message; }
    public void setTime(String time) { this.time = time; }
    public void setReceiverType(String receiverType) { this.receiverType = receiverType; }
}