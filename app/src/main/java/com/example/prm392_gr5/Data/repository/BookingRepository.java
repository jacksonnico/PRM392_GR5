package com.example.prm392_gr5.Data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Booking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingRepository {
    private final DatabaseHelper dbHelper;

    public BookingRepository(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
    }

    // Tổng số booking
    public int getBookingCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM bookings", null);
        c.moveToFirst();
        int cnt = c.getInt(0);
        c.close();
        return cnt;
    }

    public int getBookingCountByPitch(int pitchId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM bookings WHERE pitchId = ?",
                new String[]{String.valueOf(pitchId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public List<Booking> getAllBookingsWithPitchNames() {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Updated query to include user name
        String query = "SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                "FROM bookings b " +
                "JOIN pitches p ON b.pitchId = p.id " +
                "LEFT JOIN users u ON b.userId = u.id " +
                "ORDER BY b.dateTime DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor);
                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookings;
    }

    public int getBookingCountByStatus(String status) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM bookings WHERE status = ?",
                new String[]{status});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Bổ sung trong BookingRepository.java
    public Map<String, Integer> getTopPitchesByBookings(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT pi.name, COUNT(*) as booking_count " +
                "FROM bookings b " +
                "JOIN pitches pi ON b.pitchId = pi.id " +
                "GROUP BY b.pitchId " +
                "ORDER BY booking_count DESC " +
                "LIMIT ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        Map<String, Integer> result = new LinkedHashMap<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int count = cursor.getInt(1);
            result.put(name, count);
        }
        cursor.close();
        return result;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Fixed query with proper JOINs for users and services
        String query = "SELECT b.id, b.userId, b.pitchId, b.dateTime, b.services, " +
                "b.depositAmount, b.status, p.name AS pitchName, u.fullName AS userName " +
                "FROM bookings b " +
                "JOIN pitches p ON b.pitchId = p.id " +
                "LEFT JOIN users u ON b.userId = u.id " +
                "ORDER BY b.dateTime DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
                int pitchId = cursor.getInt(cursor.getColumnIndexOrThrow("pitchId"));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
                String services = cursor.getString(cursor.getColumnIndexOrThrow("services"));
                double depositAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("depositAmount"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String pitchName = cursor.getString(cursor.getColumnIndexOrThrow("pitchName"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"));

                // Get service names based on service IDs
                List<String> serviceNames = getServiceNamesByIds(services);

                // Constructor đầy đủ
                Booking booking = new Booking(id, userId, pitchId, dateTime, services, depositAmount, status, pitchName, userName, serviceNames);
                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookings;
    }

    // NEW: Helper method to get service names by IDs
    private List<String> getServiceNamesByIds(String serviceIds) {
        List<String> serviceNames = new ArrayList<>();

        if (serviceIds == null || serviceIds.trim().isEmpty()) {
            return serviceNames;
        }

        // Clean up the string: remove [ ] and spaces
        serviceIds = serviceIds.replace("[", "").replace("]", "").trim();

        String[] ids = serviceIds.split(",");

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        for (String id : ids) {
            id = id.trim();
            if (!id.isEmpty()) {
                Cursor cursor = db.rawQuery("SELECT name FROM services WHERE id = ?", new String[]{id});
                if (cursor.moveToFirst()) {
                    serviceNames.add(cursor.getString(0));
                }
                cursor.close();
            }
        }

        return serviceNames;
    }


    // NEW: Tìm kiếm booking theo từ khóa
    public List<Booking> searchBookings(String keyword) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                "FROM bookings b " +
                "JOIN pitches p ON b.pitchId = p.id " +
                "LEFT JOIN users u ON b.userId = u.id " +
                "WHERE p.name LIKE ? OR b.services LIKE ? OR b.status LIKE ? OR u.fullName LIKE ? " +
                "ORDER BY b.dateTime DESC";

        String searchTerm = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{searchTerm, searchTerm, searchTerm, searchTerm});

        if (cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor);
                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookings;
    }

    // NEW: Lọc booking theo trạng thái
    public List<Booking> filterBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                "FROM bookings b " +
                "JOIN pitches p ON b.pitchId = p.id " +
                "LEFT JOIN users u ON b.userId = u.id " +
                "WHERE b.status = ? " +
                "ORDER BY b.dateTime DESC";

        Cursor cursor = db.rawQuery(query, new String[]{status});

        if (cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor);
                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookings;
    }

    // NEW: Lấy chi tiết booking theo ID
    public Booking getBookingById(int bookingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                "FROM bookings b " +
                "JOIN pitches p ON b.pitchId = p.id " +
                "LEFT JOIN users u ON b.userId = u.id " +
                "WHERE b.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(bookingId)});

        Booking booking = null;
        if (cursor.moveToFirst()) {
            booking = createBookingFromCursor(cursor);
        }

        cursor.close();
        db.close();
        return booking;
    }

    // NEW: Cập nhật trạng thái booking
    public boolean updateBookingStatus(int bookingId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        int rowsAffected = db.update("bookings", values, "id = ?",
                new String[]{String.valueOf(bookingId)});

        db.close();
        return rowsAffected > 0;
    }

    // NEW: Lấy tất cả trạng thái có sẵn
    public List<String> getAllBookingStatuses() {
        List<String> statuses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT status FROM bookings ORDER BY status", null);

        if (cursor.moveToFirst()) {
            do {
                statuses.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return statuses;
    }

    // Helper method để tạo Booking object từ Cursor
    private Booking createBookingFromCursor(Cursor cursor) {
        Booking booking = new Booking();
        booking.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        booking.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
        booking.setPitchId(cursor.getInt(cursor.getColumnIndexOrThrow("pitchId")));
        booking.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("dateTime")));
        booking.setServices(cursor.getString(cursor.getColumnIndexOrThrow("services")));
        booking.setDepositAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("depositAmount")));
        booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        booking.setPitchName(cursor.getString(cursor.getColumnIndexOrThrow("pitchName")));

        // Set userName if available
        int userNameIndex = cursor.getColumnIndex("userName");
        if (userNameIndex != -1) {
            booking.setUserName(cursor.getString(userNameIndex));
        }

        // Get service names
        String services = cursor.getString(cursor.getColumnIndexOrThrow("services"));
        List<String> serviceNames = getServiceNamesByIds(services);
        booking.setServiceNames(serviceNames);

        return booking;
    }
    public List<Booking> getBookingsByPitch(int pitchId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                "FROM bookings b " +
                "JOIN pitches p ON b.pitchId = p.id " +
                "LEFT JOIN users u ON b.userId = u.id " +
                "WHERE b.pitchId = ?", new String[]{String.valueOf(pitchId)});

        if (cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor); // Sử dụng method chuẩn
                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookings;//comment
    }

    // Helper method




}