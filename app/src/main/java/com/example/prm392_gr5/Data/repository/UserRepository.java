package com.example.prm392_gr5.Data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final SQLiteDatabase db;

    public UserRepository(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    // Đăng nhập
    public User login(String phone, String password) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phoneNumber = ? AND password = ?", new String[]{phone, password});
        if (cursor.moveToFirst()) {
            return mapCursorToUser(cursor);
        }
        cursor.close();
        return null;
    }

    // Kiểm tra số điện thoại đã tồn tại
    public boolean isPhoneExists(String phone) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phoneNumber = ?", new String[]{phone});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Đăng ký tài khoản mới
    public boolean register(User user) {
        if (isPhoneExists(user.getPhoneNumber())) return false;
        ContentValues values = new ContentValues();
        values.put("fullName", user.getFullName());
        values.put("phoneNumber", user.getPhoneNumber());
        values.put("password", user.getPassword());
        values.put("role", user.getRole());
        values.put("isActive", 1); // default active
        return db.insert("users", null, values) != -1;
    }

    // Khôi phục mật khẩu
    public boolean resetPassword(String phone, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        return db.update("users", values, "phoneNumber = ?", new String[]{phone}) > 0;
    }

    // Thêm người dùng (dành cho admin)
    public long addUser(User u) {
        ContentValues cv = new ContentValues();
        cv.put("fullName", u.getFullName());
        cv.put("phoneNumber", u.getPhoneNumber());
        cv.put("password", u.getPassword());
        cv.put("role", u.getRole());
        cv.put("isActive", u.isActive() ? 1 : 0);
        return db.insert("users", null, cv);
    }

    // Cập nhật người dùng
    public int updateUser(User u) {
        ContentValues cv = new ContentValues();
        cv.put("fullName", u.getFullName());
        cv.put("phoneNumber", u.getPhoneNumber());
        cv.put("password", u.getPassword());
        cv.put("role", u.getRole());
        return db.update("users", cv, "id=?", new String[]{String.valueOf(u.getId())});
    }

    // Vô hiệu hóa (khóa) người dùng
    public int setUserInactive(int userId) {
        ContentValues cv = new ContentValues();
        cv.put("isActive", 0);
        return db.update("users", cv, "id=?", new String[]{String.valueOf(userId)});
    }

    // Mở khóa người dùng
    public int setUserActive(int userId) {
        ContentValues cv = new ContentValues();
        cv.put("isActive", 1);
        return db.update("users", cv, "id=?", new String[]{String.valueOf(userId)});
    }

    // Lấy toàn bộ người dùng
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        Cursor c = db.query("users", null, null, null, null, null, "id DESC");
        while (c.moveToNext()) {
            list.add(mapCursorToUser(c));
        }
        c.close();
        return list;
    }

    // Lấy người dùng theo ID
    public User getUserById(int userId) {
        Cursor cursor = db.query("users", null, "id = ?", new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            User user = mapCursorToUser(cursor);
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    // Lấy người dùng theo số điện thoại
    public User getUserByPhone(String phone) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phoneNumber = ?", new String[]{phone});
        if (cursor.moveToFirst()) {
            User user = mapCursorToUser(cursor);
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    // Lấy danh sách người dùng theo vai trò
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        Cursor cursor = db.query("users", null, "role = ?", new String[]{role},
                null, null, "fullName ASC");

        while (cursor.moveToNext()) {
            users.add(mapCursorToUser(cursor));
        }
        cursor.close();
        return users;
    }

    // Đếm người dùng theo role
    public int getUserCountByRole(String role) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM users WHERE role=?", new String[]{role});
        c.moveToFirst();
        int cnt = c.getInt(0);
        c.close();
        return cnt;
    }

    // Helper để mapping cursor → User
    private User mapCursorToUser(Cursor cursor) {
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("role")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isActive")) == 1
        );
    }
}
