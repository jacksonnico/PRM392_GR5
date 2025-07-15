package com.example.prm392_gr5.Data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationManagerRepository {
    private final DatabaseHelper dbHelper;

    public NotificationManagerRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Thêm thông báo mới
    public void addNotification(String message, String dateTime, int receiverId, String receiverType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("message", message);
            values.put("dateTime", dateTime);
            values.put("receiverId", receiverId);
            values.put("receiverType", receiverType);
            values.put("isRead", 0); // 👈 Mặc định chưa đọc
            db.insert(DatabaseHelper.TBL_NOTIFICATIONS, null, values);
        } finally {
            db.close();
        }
    }

    // Lấy danh sách thông báo của User
    public List<Notification> getNotifications(int receiverId, String receiverType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        List<Notification> list = new ArrayList<>();
        try {
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TBL_NOTIFICATIONS +
                            " WHERE receiverId = ? AND receiverType = ? ORDER BY id DESC",
                    new String[]{String.valueOf(receiverId), receiverType});
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String msg = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
                int isRead = cursor.getInt(cursor.getColumnIndexOrThrow("isRead"));
                Notification notification = new Notification(id, msg, time, receiverId, receiverType);
                notification.setRead(isRead == 1);
                list.add(notification);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    // Lấy danh sách thông báo Owner
    public List<Notification> getNotificationsForOwner() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        List<Notification> list = new ArrayList<>();
        try {
            String query = "SELECT n.*, u.fullName AS userName FROM " + DatabaseHelper.TBL_NOTIFICATIONS +
                    " n JOIN " + DatabaseHelper.TBL_USERS + " u ON n.receiverId = u.id " +
                    "WHERE n.receiverType = 'user' ORDER BY n.id DESC";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"));
                int isRead = cursor.getInt(cursor.getColumnIndexOrThrow("isRead"));

                Notification notification = new Notification(id, message, dateTime, 0, "user");
                notification.setUserName(userName);
                notification.setRead(isRead == 1);
                list.add(notification);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    // Đếm số thông báo chưa đọc
    public int getUnreadCount(int receiverId, String receiverType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TBL_NOTIFICATIONS +
                            " WHERE receiverId = ? AND receiverType = ? AND isRead = 0",
                    new String[]{String.valueOf(receiverId), receiverType});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return count;
    }

    // Đánh dấu tất cả thông báo đã đọc
    public void markAllAsRead(int receiverId, String receiverType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("isRead", 1);
            db.update(DatabaseHelper.TBL_NOTIFICATIONS, values,
                    "receiverId = ? AND receiverType = ?",
                    new String[]{String.valueOf(receiverId), receiverType});
        } finally {
            db.close();
        }
    }
}
