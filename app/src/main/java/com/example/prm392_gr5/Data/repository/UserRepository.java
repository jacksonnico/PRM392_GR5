package com.example.prm392_gr5.Data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.User;

public class UserRepository {
    private final SQLiteDatabase db;

    public UserRepository(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public User login(String phone, String password) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phoneNumber = ? AND password = ?", new String[]{phone, password});
        if (cursor.moveToFirst()) {
            return mapCursorToUser(cursor);
        }
        return null;
    }

    public boolean isPhoneExists(String phone) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phoneNumber = ?", new String[]{phone});
        return cursor.moveToFirst();
    }

    public boolean register(User user) {
        if (isPhoneExists(user.getPhoneNumber())) return false;
        ContentValues values = new ContentValues();
        values.put("fullName", user.getFullName());
        values.put("phoneNumber", user.getPhoneNumber());
        values.put("password", user.getPassword());
        values.put("role", user.getRole()); // ✅ Đúng
        return db.insert("users", null, values) != -1;
    }

    public boolean resetPassword(String phone, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        return db.update("users", values, "phoneNumber = ?", new String[]{phone}) > 0;
    }

    public User getUserByPhone(String phone) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phoneNumber = ?", new String[]{phone});
        if (cursor.moveToFirst()) {
            return mapCursorToUser(cursor);
        }
        return null;
    }

    private User mapCursorToUser(Cursor cursor) {
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("role"))
        );
    }
    public boolean changePassword(String phone, String oldPassword, String newPassword) {
        // Kiểm tra xem mật khẩu cũ có đúng không
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phoneNumber = ? AND password = ?", new String[]{phone, oldPassword});
        if (!cursor.moveToFirst()) {
            return false; // Mật khẩu cũ không đúng
        }

        // Nếu đúng thì cập nhật mật khẩu mới
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        return db.update("users", values, "phoneNumber = ?", new String[]{phone}) > 0;
    }

}
