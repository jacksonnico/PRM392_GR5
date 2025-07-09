package com.example.prm392_gr5.Data.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Booking {
    private int id;
    private int userId;
    private int pitchId;
    private String dateTime;
    private String timeSlot;
    private String services;
    private String status;
    private String pitchName;
    private String userName;
    private List<String> serviceNames;

    public Booking() {
        this.serviceNames = new ArrayList<>();
    }

    public Booking(int id,
                   int userId,
                   int pitchId,
                   String dateTime,
                   String timeSlot,
                   String services,
                   String status,
                   String pitchName,
                   String userName,
                   List<String> serviceNames) {
        this.id = id;
        this.userId = userId;
        this.pitchId = pitchId;
        this.dateTime = dateTime;
        this.timeSlot = timeSlot;
        this.services = services;
        this.status = status;
        this.pitchName = pitchName;
        this.userName = userName;
        this.serviceNames = serviceNames != null ? serviceNames : new ArrayList<>();
    }

    public Booking(int id,
                   int userId,
                   int pitchId,
                   String dateTime,
                   String timeSlot,
                   String services,
                   String status) {
        this(id, userId, pitchId, dateTime, timeSlot, services, status, "", "", new ArrayList<>());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPitchId() { return pitchId; }
    public void setPitchId(int pitchId) { this.pitchId = pitchId; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPitchName() { return pitchName; }
    public void setPitchName(String pitchName) { this.pitchName = pitchName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public List<String> getServiceNames() { return serviceNames; }
    public String getFormattedDate() {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
            Date date = input.parse(this.dateTime);
            return output.format(date);
        } catch (Exception e) {
            return dateTime.replace("T", " ");
        }
    }

    public void setServiceNames(List<String> serviceNames) { this.serviceNames = serviceNames; }
}


