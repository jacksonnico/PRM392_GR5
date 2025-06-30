package com.example.prm392_gr5.Data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentRepository {
    private final DatabaseHelper dbHelper;

    public PaymentRepository(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);//comment
    }

    // Tổng doanh thu (chỉ tính payments.status = 'completed')
    public double getTotalRevenue() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(amount) FROM payments WHERE status='completed'",
                null);
        c.moveToFirst();
        double sum = c.isNull(0) ? 0 : c.getDouble(0);
        c.close();
        return sum;
    }

    public double getRevenueByTimeRange(String range) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query;

        switch (range) {
            case "week":
                query = "SELECT SUM(amount) FROM payments " +
                        "WHERE status='completed' AND createdAt >= date('now', '-7 day')";
                break;
            case "month":
                query = "SELECT SUM(amount) FROM payments " +
                        "WHERE status='completed' AND strftime('%Y-%m', createdAt) = strftime('%Y-%m', 'now')";
                break;
            case "quarter":
                query = "SELECT SUM(amount) FROM payments " +
                        "WHERE status='completed' AND strftime('%Y', createdAt) = strftime('%Y', 'now') " +
                        "AND ((cast(strftime('%m', createdAt) as integer) - 1) / 3 + 1) = ((cast(strftime('%m', 'now') as integer) - 1) / 3 + 1)";
                break;
            case "year":
                query = "SELECT SUM(amount) FROM payments " +
                        "WHERE status='completed' AND strftime('%Y', createdAt) = strftime('%Y', 'now')";
                break;
            default:
                return 0;
        }

        Cursor c = db.rawQuery(query, null);
        double sum = 0;
        if (c.moveToFirst()) {
            sum = c.isNull(0) ? 0 : c.getDouble(0);
        }
        c.close();
        return sum;
    }




    // Doanh thu theo từng owner
    public Map<String, Double> getRevenueByOwner() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT u.fullName, SUM(p.amount) as revenue " +
                "FROM payments p " +
                "JOIN bookings b ON p.bookingId = b.id " +
                "JOIN pitches pi ON b.pitchId = pi.id " +
                "JOIN users u ON pi.ownerId = u.id " +
                "WHERE p.status = 'completed' " +
                "GROUP BY u.id";
        Cursor cursor = db.rawQuery(query, null);

        Map<String, Double> map = new LinkedHashMap<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            double revenue = cursor.getDouble(1);
            map.put(name, revenue);
        }
        cursor.close();
        return map;
    }
}
