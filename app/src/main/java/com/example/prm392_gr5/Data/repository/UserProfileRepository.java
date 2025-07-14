package com.example.prm392_gr5.Data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
            cursor = db.rawQuery("SELECT fullName FROM " + DatabaseHelper.TBL_USERS + " WHERE id = ?", new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
            }
            return "User_" + userId;
        } catch (Exception e) {
            e.printStackTrace();
            return "User_" + userId;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    public String getPhoneNumberFromUserId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT phoneNumber FROM " + DatabaseHelper.TBL_USERS + " WHERE id = ?", new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"));
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    public String getUserNameAndPhone(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT fullName, phoneNumber FROM " + DatabaseHelper.TBL_USERS + " WHERE id = ?", new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"));
                return name + " - " + phone;
            }
            return "Người dùng không xác định";
        } catch (Exception e) {
            e.printStackTrace();
            return "Người dùng không xác định";
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}