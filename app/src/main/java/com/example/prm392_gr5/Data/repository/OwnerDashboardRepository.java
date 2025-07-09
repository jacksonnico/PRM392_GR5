package com.example.prm392_gr5.Data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;

public class OwnerDashboardRepository {
    private final DatabaseHelper dbHelper;

    public OwnerDashboardRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public int getTotalPitches(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM pitches WHERE ownerId = ?", new String[]{String.valueOf(ownerId)});
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        db.close();
        return count;
    }

    public int getTotalBookings(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM bookings b JOIN pitches p ON b.pitchId = p.id WHERE p.ownerId = ?", new String[]{String.valueOf(ownerId)});
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        db.close();
        return count;
    }

    public int getPendingBookings(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM bookings b JOIN pitches p ON b.pitchId = p.id WHERE p.ownerId = ? AND b.status = 'pending'", new String[]{String.valueOf(ownerId)});
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        db.close();
        return count;
    }

    public double getTotalRevenue(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(pay.amount) FROM payments pay " +
                        "JOIN bookings b ON pay.bookingId = b.id " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "WHERE p.ownerId = ? AND pay.status = 'completed'", new String[]{String.valueOf(ownerId)});
        double total = cursor.moveToFirst() ? cursor.getDouble(0) : 0;
        cursor.close();
        db.close();
        return total;
    }
    public double getTotalCompletedRevenue(int ownerId) {
        double revenue = 0.0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(pay.amount) FROM payments pay " +
                        "JOIN bookings b ON pay.bookingId = b.id " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "WHERE p.ownerId = ? AND pay.status = 'completed'",
                new String[]{String.valueOf(ownerId)});
        if (cursor.moveToFirst()) {
            revenue = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return revenue;
    }

    public int getTotalCompletedBookings(int ownerId) {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM bookings b " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "WHERE p.ownerId = ? AND b.status = 'approved'",
                new String[]{String.valueOf(ownerId)});
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}
