package com.example.prm392_gr5.Data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.Data.model.Notification;
import com.example.prm392_gr5.Data.model.UserMessageSummary;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PitchBooking.db";
    private static final int DATABASE_VERSION = 6;

    public static final String TBL_USERS = "users";
    public static final String TBL_OWNERS = "owners";
    public static final String TBL_MESSAGES = "messages";
    public static final String TBL_PITCHES = "pitches";
    public static final String TBL_BOOKINGS = "bookings";
    public static final String TBL_NOTIFICATIONS = "notifications";
    public static final String TBL_PAYMENTS = "payments";
    public static final String TBL_SERVICES = "services";
    //

    private final Context context; // Thêm biến Context

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; // Lưu Context
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fullName TEXT, " +
                "phoneNumber TEXT, " +
                "password TEXT, " +
                "role TEXT, " +
                "isActive INTEGER DEFAULT 1)");

        // Tạo bảng owners
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_OWNERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, fullName TEXT, phoneNumber TEXT, password TEXT, role TEXT)");

        // Tạo bảng messages
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_MESSAGES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, message TEXT, time TEXT, pitchName TEXT, userId INTEGER)");

        // Tạo bảng pitches
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_PITCHES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, ownerId INTEGER, name TEXT, price REAL, address TEXT, " +
                "phoneNumber TEXT, openTime TEXT, closeTime TEXT, imageUrl TEXT, status TEXT DEFAULT 'active')");

        // Tạo bảng bookings
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_BOOKINGS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pitchId INTEGER, " +
                "userId INTEGER, " +
                "dateTime TEXT, " +
                "status TEXT, " +
                "depositAmount REAL DEFAULT 0, " +
                "services TEXT, " +
                "timeSlot TEXT, " +
                "FOREIGN KEY(pitchId) REFERENCES pitches(id), " +
                "FOREIGN KEY(userId) REFERENCES users(id))");

        // Tạo bảng notifications
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NOTIFICATIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "message TEXT, " +
                "dateTime TEXT, " +
                "receiverId INTEGER, " +
                "receiverType TEXT)");

        // Tạo bảng payments
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_PAYMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bookingId INTEGER NOT NULL, " +
                "method TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "status TEXT NOT NULL, " +
                "createdAt TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(bookingId) REFERENCES " + TBL_BOOKINGS + "(id))");

        // Tạo bảng services
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_SERVICES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pitchId INTEGER, " +
                "name TEXT, " +
                "price REAL, " +
                "FOREIGN KEY(pitchId) REFERENCES pitches(id))");

        // Dữ liệu mẫu
        // USERS (4 người dùng: 2 owner, 1 user, 1 admin)
        db.execSQL("INSERT INTO " + TBL_USERS + " (fullName, phoneNumber, password, role, isActive) VALUES " +
                "('Nguyễn Xuân Chiến', '0836663285', 'chien2003', 'user', 1)," +
                "('Đỗ Văn Mạnh', '0987654321', '0987654321', 'owner', 1)," +
                "('Admin', '0999999999', 'adminpass', 'admin', 1)," +
                "('Trần Thị Lan', '0911222333', 'lanpass', 'owner', 1)");

// OWNERS (khớp id với USERS phía trên)
        db.execSQL("INSERT INTO " + TBL_OWNERS + " (id, fullName, phoneNumber, password, role) VALUES " +
                "(4, 'Trần Thị Lan', '0911222333', 'lanpass', 'owner')," +
                "(2, 'Đỗ Văn Mạnh', '0987654321', '0987654321', 'owner')");
        db.execSQL("INSERT INTO " + TBL_OWNERS + " (fullName, phoneNumber, password, role) VALUES " +
                "('Nguyen Van Chien', '0398263126', '123456', 'owner')");

// PITCHES (1 sân của Mạnh, 2 sân của Lan)
        db.execSQL("INSERT INTO " + TBL_PITCHES + " (ownerId, name, price, address, phoneNumber, openTime, closeTime, imageUrl) VALUES " +
                "(2, 'Sân Bóng Đá Bao Cấp', 500000, 'Hoa Lac Hi-tech Park, Tân Xá, Hà Nội', '0979504194', '06:00', '22:00', 'https://afd.com.vn/images/image/tin/co-san-bong.jpg')," +
                "(4, 'Sân Bóng Đại Học Quốc Gia', 550000, 'Tuyến đường Việt Nhật, Thạch Hoà, Hà Nội, 13100', '0961150113', '07:00', '21:00', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQA_gd_yXrzRrWWhzxAKf-XKPWdzayju_O7ig&s.jpg')," +
                "(2, 'Sân Bóng 5 Cửa Ô', 500000, '2GHX+425, Tân Xá, Thạch Thất, Hà Nội', '0979504194', '06:00', '22:00', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCcMUCBLWw2FVmuRYVJbkQc2TvZ5lVGwTHEQ&s.jpg')");

// MESSAGES
        db.execSQL("INSERT INTO " + TBL_MESSAGES + " (sender, message, time, pitchName, userId) VALUES " +
                "('1', 'Đặt sân lúc 10h', '12:00, 10/07/2025', 'Sân Bóng Đá Bao Cấp', 1)," +
                "('0', 'Đã nhận, xác nhận nhé!', '12:01, 10/07/2025', 'Sân Bóng Đá Bao Cấp', 0)");

// BOOKINGS (3 booking cho sân của Mạnh - pitchId = 1)
        db.execSQL("INSERT INTO " + TBL_BOOKINGS + " (pitchId, userId, dateTime, status, depositAmount, services, timeSlot) VALUES " +
                "(1, 1, '2025-07-10T10:00:00', 'confirmed', 200000.0, '', '10:00 - 11:00')," +
                "(1, 1, '2025-07-11T10:00:00', 'confirmed', 200000.0, '', '10:00 - 11:00')," +
                "(1, 1, '2025-07-12T10:00:00', 'confirmed', 200000.0, '', '10:00 - 11:00')");
        db.execSQL("INSERT INTO " + TBL_BOOKINGS + " (pitchId, userId, dateTime, status, depositAmount, services) VALUES " +
                "(1, 1, '2025-07-10T10:00:00', 'confirmed', 200000.0, '')");

        db.execSQL("INSERT INTO " + TBL_PAYMENTS + " (bookingId, method, amount, status) VALUES " +
                "(1, 'cash', 500000, 'completed')");

        db.execSQL("INSERT INTO " + TBL_NOTIFICATIONS + " (message, dateTime, receiverId, receiverType) VALUES " +
                "('Đặt sân thành công', '12:02, 10/07/2025', 1, 'user')," +
                "('Xác nhận thanh toán', '12:03, 10/07/2025', 1, 'user')");

// SERVICES (dịch vụ cho sân 1 và 2)
        db.execSQL("INSERT INTO " + TBL_SERVICES + " (pitchId, name, price) VALUES " +
                "(1, 'Thuê bóng', 50000)," +
                "(1, 'Nước uống', 10000)," +
                "(2, 'Thuê bóng', 60000)");

// PAYMENTS (3 khoản thanh toán tương ứng 3 booking)
        db.execSQL("INSERT INTO " + TBL_PAYMENTS + " (bookingId, method, amount, status) VALUES " +
                "(1, 'cash', 200000, 'completed')," +
                "(2, 'momo', 250000, 'completed')," +
                "(3, 'banking', 300000, 'completed')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TBL_BOOKINGS + " ADD COLUMN depositAmount REAL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TBL_BOOKINGS + " ADD COLUMN services TEXT");
            db.execSQL("ALTER TABLE " + TBL_BOOKINGS + " ADD COLUMN timeSlot TEXT");
        }
    }

    // Thêm phương thức getContext
    public Context getContext() {
        return context;
    }
}