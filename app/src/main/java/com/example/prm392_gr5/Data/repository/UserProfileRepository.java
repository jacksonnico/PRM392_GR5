package com.example.prm392_gr5.Data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.prm392_gr5.Data.db.DatabaseHelper;

public class UserProfileRepository {
    private final DatabaseHelper dbHelper;

    public UserProfileRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public String getFullNameFromUserId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT fullName FROM users WHERE id = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
            }
            return "Khách hàng";
        } catch (Exception e) {
            Log.e("UserProfileRepository", "Error getting user name for id " + userId + ": " + e.getMessage());
            return "Khách hàng";
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    public String getPhoneNumberFromUserId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT phoneNumber FROM users WHERE id = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"));
            }
            return null;
        } catch (Exception e) {
            Log.e("UserProfileRepository", "Error getting phone number for id " + userId + ": " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}