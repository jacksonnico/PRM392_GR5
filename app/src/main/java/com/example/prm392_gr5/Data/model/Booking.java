package com.example.prm392_gr5.Data.model;

public class Booking {
    private int id;
    private int userId;
    private int pitchId;
    private String dateTime;
    private String services;
    private double depositAmount;
    private String status;

    public Booking() {}

    public Booking(int id, int userId, int pitchId, String dateTime,
                   String services, double depositAmount, String status) {
        this.id = id;
        this.userId = userId;
        this.pitchId = pitchId;
        this.dateTime = dateTime;
        this.services = services;
        this.depositAmount = depositAmount;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPitchId() { return pitchId; }
    public void setPitchId(int pitchId) { this.pitchId = pitchId; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }

    public double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(double depositAmount) { this.depositAmount = depositAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

