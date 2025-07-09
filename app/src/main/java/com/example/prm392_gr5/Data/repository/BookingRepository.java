package com.example.prm392_gr5.Data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Booking;
import com.example.prm392_gr5.Data.model.Payment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingRepository {
    private final DatabaseHelper dbHelper;

    public BookingRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // Thêm booking mới
    public long addBooking(Booking b) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userId", b.getUserId());
        cv.put("pitchId", b.getPitchId());
        cv.put("dateTime", b.getDateTime());
        cv.put("services", b.getServices());
        cv.put("depositAmount", b.getDepositAmount());
        cv.put("status", b.getStatus());
        long id = db.insert("bookings", null, cv);
        db.close();
        return id;
    }

    // Lấy tất cả booking của 1 user, sắp xếp theo dateTime DESC
    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                "bookings", null,
                "userId=?",
                new String[]{String.valueOf(userId)},
                null, null,
                "dateTime DESC"
        );
        while (c.moveToNext()) {
            Booking b = new Booking();
            b.setId(c.getInt(c.getColumnIndexOrThrow("id")));
            b.setUserId(userId);
            b.setPitchId(c.getInt(c.getColumnIndexOrThrow("pitchId")));
            b.setDateTime(c.getString(c.getColumnIndexOrThrow("dateTime")));
            b.setServices(c.getString(c.getColumnIndexOrThrow("services")));
            b.setDepositAmount(c.getDouble(c.getColumnIndexOrThrow("depositAmount")));
            b.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
            list.add(b);
        }
        c.close();
        db.close();
        return list;
    }

    // Lấy tất cả booking của 1 pitch
    public List<Booking> getBookingsByPitch(int pitchId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                        "FROM bookings b " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "LEFT JOIN users u ON b.userId = u.id " +
                        "WHERE b.pitchId = ? " +
                        "ORDER BY b.dateTime DESC",
                new String[]{String.valueOf(pitchId)}
        );
        while (cursor.moveToNext()) {
            bookings.add(createBookingFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return bookings;
    }

    // Lấy chi tiết booking theo ID
    public Booking getBookingById(int bookingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                        "FROM bookings b " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "LEFT JOIN users u ON b.userId = u.id " +
                        "WHERE b.id = ?",
                new String[]{String.valueOf(bookingId)}
        );
        Booking booking = null;
        if (cursor.moveToFirst()) {
            booking = createBookingFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return booking;
    }

    // Lấy tất cả booking (với tên pitch và user)
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT b.id, b.userId, b.pitchId, b.dateTime, b.services, " +
                        "b.depositAmount, b.status, p.name AS pitchName, u.fullName AS userName " +
                        "FROM bookings b " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "LEFT JOIN users u ON b.userId = u.id " +
                        "ORDER BY b.dateTime DESC",
                null
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
            int pitchId = cursor.getInt(cursor.getColumnIndexOrThrow("pitchId"));
            String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
            String services = cursor.getString(cursor.getColumnIndexOrThrow("services"));
            double depositAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("depositAmount"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String pitchName = cursor.getString(cursor.getColumnIndexOrThrow("pitchName"));
            String userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"));

            List<String> serviceNames = getServiceNamesByIds(services);
            Booking booking = new Booking(
                    id, userId, pitchId, dateTime, services,
                    depositAmount, status, pitchName, userName, serviceNames
            );
            bookings.add(booking);
        }
        cursor.close();
        db.close();
        return bookings;
    }

    // Tìm kiếm booking theo từ khóa trên pitch.name, services, status, user.fullName
    public List<Booking> searchBookings(String keyword) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String term = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(
                "SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                        "FROM bookings b " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "LEFT JOIN users u ON b.userId = u.id " +
                        "WHERE p.name LIKE ? OR b.services LIKE ? OR b.status LIKE ? OR u.fullName LIKE ? " +
                        "ORDER BY b.dateTime DESC",
                new String[]{term, term, term, term}
        );
        while (cursor.moveToNext()) {
            bookings.add(createBookingFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return bookings;
    }

    // Lọc booking theo status
    public List<Booking> filterBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                        "FROM bookings b " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "LEFT JOIN users u ON b.userId = u.id " +
                        "WHERE b.status = ? " +
                        "ORDER BY b.dateTime DESC",
                new String[]{status}
        );
        while (cursor.moveToNext()) {
            bookings.add(createBookingFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return bookings;
    }

    // Lấy danh sách status có trong bookings
    public List<String> getAllBookingStatuses() {
        List<String> statuses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT status FROM bookings ORDER BY status",
                null
        );
        while (cursor.moveToNext()) {
            statuses.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return statuses;
    }

    // Hủy booking
    public boolean cancelBooking(int bookingId) {
        return updateBookingStatus(bookingId, "cancelled");
    }

    // Cập nhật status booking
    public boolean updateBookingStatus(int bookingId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        int rows = db.update("bookings", cv, "id=?", new String[]{String.valueOf(bookingId)});
        db.close();
        return rows > 0;
    }

    // Xóa hết booking của user
    public boolean deleteAllBookingsByUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("bookings", "userId=?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    // Thêm payment
    public long addPayment(Payment p) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("bookingId", p.getBookingId());
        cv.put("method", p.getMethod());
        cv.put("amount", p.getAmount());
        cv.put("status", p.getStatus());
        long id = db.insert("payments", null, cv);
        db.close();
        return id;
    }

    // Lấy danh sách payments theo bookingId
    public List<Payment> getPaymentsByBooking(int bookingId) {
        List<Payment> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                "payments", null,
                "bookingId=?",
                new String[]{String.valueOf(bookingId)},
                null, null,
                "createdAt DESC"
        );
        while (c.moveToNext()) {
            Payment p = new Payment();
            p.setId(c.getInt(c.getColumnIndexOrThrow("id")));
            p.setBookingId(bookingId);
            p.setMethod(c.getString(c.getColumnIndexOrThrow("method")));
            p.setAmount(c.getDouble(c.getColumnIndexOrThrow("amount")));
            p.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
            p.setCreatedAt(c.getString(c.getColumnIndexOrThrow("createdAt")));
            list.add(p);
        }
        c.close();
        db.close();
        return list;
    }

    // Tổng số booking
    public int getBookingCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM bookings", null);
        c.moveToFirst();
        int cnt = c.getInt(0);
        c.close();
        db.close();
        return cnt;
    }

    // Tổng số booking theo pitch
    public int getBookingCountByPitch(int pitchId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM bookings WHERE pitchId = ?",
                new String[]{String.valueOf(pitchId)}
        );
        c.moveToFirst();
        int cnt = c.getInt(0);
        c.close();
        db.close();
        return cnt;
    }

    // Tổng số booking theo status
    public int getBookingCountByStatus(String status) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM bookings WHERE status = ?",
                new String[]{status}
        );
        c.moveToFirst();
        int cnt = c.getInt(0);
        c.close();
        db.close();
        return cnt;
    }

    // Top pitches theo số booking
    public Map<String, Integer> getTopPitchesByBookings(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT p.name, COUNT(*) AS booking_count " +
                        "FROM bookings b " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "GROUP BY b.pitchId " +
                        "ORDER BY booking_count DESC " +
                        "LIMIT ?",
                new String[]{String.valueOf(limit)}
        );
        Map<String, Integer> result = new LinkedHashMap<>();
        while (cursor.moveToNext()) {
            result.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
        db.close();
        return result;
    }

    // Tạo đối tượng Booking từ Cursor (có pitchName, userName, serviceNames)
    private Booking createBookingFromCursor(Cursor cursor) {
        Booking b = new Booking();
        b.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        b.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
        b.setPitchId(cursor.getInt(cursor.getColumnIndexOrThrow("pitchId")));
        b.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("dateTime")));
        b.setServices(cursor.getString(cursor.getColumnIndexOrThrow("services")));
        b.setDepositAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("depositAmount")));
        b.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        b.setPitchName(cursor.getString(cursor.getColumnIndexOrThrow("pitchName")));
        int idx = cursor.getColumnIndex("userName");
        if (idx != -1) {
            b.setUserName(cursor.getString(idx));
        }
        b.setServiceNames(getServiceNamesByIds(b.getServices()));
        return b;
    }

    // Helper: chuyển serviceIds thành danh sách tên
    private List<String> getServiceNamesByIds(String serviceIds) {
        List<String> names = new ArrayList<>();
        if (serviceIds == null || serviceIds.trim().isEmpty()) {
            return names;
        }
        serviceIds = serviceIds.replace("[", "").replace("]", "").trim();
        String[] ids = serviceIds.split(",");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        for (String id : ids) {
            id = id.trim();
            if (!id.isEmpty()) {
                Cursor c = db.rawQuery(
                        "SELECT name FROM services WHERE id = ?",
                        new String[]{id}
                );
                if (c.moveToFirst()) {
                    names.add(c.getString(0));
                }
                c.close();
            }
        }
        db.close();
        return names;
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
    public List<Booking> getPendingBookings(int ownerId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT b.*, p.name AS pitchName, u.fullName AS userName " +
                        "FROM bookings b " +
                        "JOIN pitches p ON b.pitchId = p.id " +
                        "LEFT JOIN users u ON b.userId = u.id " +
                        "WHERE p.ownerId = ? AND b.status = 'pending' " +
                        "ORDER BY b.dateTime DESC",
                new String[]{String.valueOf(ownerId)}
        );
        while (cursor.moveToNext()) {
            bookings.add(createBookingFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return bookings;
    }
    public String getServiceText(String servicesJson) {
        List<String> serviceNames = getServiceNamesByIds(servicesJson);
        return String.join(", ", serviceNames);
    }
}
