package com.example.prm392_gr5.Data.model;

public class  Payment {
    private int id;
    private int bookingId;
    private String method;
    private double amount;
    private String status;
    private String createdAt;

    public Payment() {}

    public Payment(int id, int bookingId, String method, double amount,
                   String status, String createdAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

