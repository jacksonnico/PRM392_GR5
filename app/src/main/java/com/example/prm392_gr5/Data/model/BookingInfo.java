package com.example.prm392_gr5.Data.model;

public class BookingInfo {
    public int id, userId, pitchId;
    public String dateTime, services, status, pitchName, userName, userPhone;
    public double depositAmount;
    public BookingInfo(Booking booking) {
        this.id = booking.getId();
        this.userName = booking.getUserName();
        this.pitchName = booking.getPitchName();
        this.dateTime = booking.getDateTime();
        this.depositAmount = booking.getDepositAmount();
        this.services = booking.getServices();
        this.pitchId = booking.getPitchId();
    }

}