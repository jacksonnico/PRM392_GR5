package com.example.prm392_gr5.Data.model;

import java.util.List;

/**
 * Wrapper model combining Booking and related Pitch/User info for display
 */
public class BookingInfo {
    public int id;
    public int userId;
    public int pitchId;
    public String dateTime;

    public String services;
    public String timeSlot;
    public String status;

    public String pitchName;
    public String pitchAddress;
    public String pitchPhone;
    public double pitchPrice;

    public String userName;
    public String userPhone;

    public List<String> serviceNames;

    /**
     * Construct BookingInfo from Booking and Pitch models
     */
    public BookingInfo(Booking booking, Pitch pitch) {
        this.id         = booking.getId();
        this.userId     = booking.getUserId();
        this.pitchId    = booking.getPitchId();
        this.dateTime   = booking.getDateTime();
        this.timeSlot   = booking.getTimeSlot();
        this.services   = booking.getServices();
        this.status     = booking.getStatus();

        // Pitch details
        this.pitchName    = pitch.getName();
        this.pitchAddress = pitch.getAddress();
        this.pitchPhone   = pitch.getPhoneNumber();
        this.pitchPrice   = pitch.getPrice();

        // User info
        this.userName  = booking.getUserName();
        this.userPhone = booking.getUserName(); // replace if you store phone separately

        // Parsed service names
        this.serviceNames = booking.getServiceNames();
    }

    // Optionally add getters if needed
}
