package com.example.prm392_gr5.Data.model;

public class Pitch {
    private int id;
    private int ownerId;
    private String name;
    private double price;
    private String address;
    private String phoneNumber;
    private String openTime;
    private String closeTime;
    private String imageUrl;
    private String status;//comment


    public Pitch() {}

    public Pitch(int id, int ownerId, String name, double price,
                 String address, String phoneNumber,
                 String openTime, String closeTime, String imageUrl,String status) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.price = price;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }

    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}