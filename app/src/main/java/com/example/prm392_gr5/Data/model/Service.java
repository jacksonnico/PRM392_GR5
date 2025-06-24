package com.example.prm392_gr5.Data.model;

public class Service {
    private int id;
    private int pitchId;
    private String name;
    private double price;

    public Service() {}

    public Service(int id, int pitchId, String name, double price) {
        this.id = id;
        this.pitchId = pitchId;
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPitchId() { return pitchId; }
    public void setPitchId(int pitchId) { this.pitchId = pitchId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}