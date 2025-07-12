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

    public void addNotification(String message, String dateTime, int receiverId, String receiverType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("message", message);
            values.put("dateTime", dateTime);
            values.put("receiverId", receiverId);
            values.put("receiverType", receiverType);
            db.insert(DatabaseHelper.TBL_NOTIFICATIONS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public List<Notification> getNotifications(int receiverId, String receiverType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        List<Notification> list = new ArrayList<>();
        try {
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TBL_NOTIFICATIONS + " WHERE receiverId = ? AND receiverType = ? ORDER BY id DESC",
                    new String[]{String.valueOf(receiverId), receiverType});
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String msg = cursor.getString(1);
                String time = cursor.getString(2);
                list.add(new Notification(id, msg, time, receiverId, receiverType));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }

    public List<Notification> getNotificationsForOwner() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        List<Notification> list = new ArrayList<>();
        try {
            String query = "SELECT n.*, u.fullName AS userName FROM " + DatabaseHelper.TBL_NOTIFICATIONS + " n " +
                    "JOIN " + DatabaseHelper.TBL_USERS + " u ON n.receiverId = u.id " +
                    "WHERE n.receiverType = 'user' ORDER BY n.id DESC";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"));
                Notification notification = new Notification(id, message, dateTime, 0, "user");
                notification.setUserName(userName);
                list.add(notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return list;
    }
}