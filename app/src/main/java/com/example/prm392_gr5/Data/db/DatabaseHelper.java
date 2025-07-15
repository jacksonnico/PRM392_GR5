package com.example.prm392_gr5.Data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PitchBooking.db";
    private static final int DATABASE_VERSION = 8; // ✅ Tăng version

    public static final String TBL_USERS = "users";
    public static final String TBL_OWNERS = "owners";
    public static final String TBL_MESSAGES = "messages";
    public static final String TBL_PITCHES = "pitches";
    public static final String TBL_BOOKINGS = "bookings";
    public static final String TBL_NOTIFICATIONS = "notifications";
    public static final String TBL_PAYMENTS = "payments";
    public static final String TBL_SERVICES = "services";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
                "id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, message TEXT, time TEXT, pitchName TEXT, userId INTEGER, ownerId INTEGER)");

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

        // ✅ Tạo bảng notifications có thêm isRead
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NOTIFICATIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "message TEXT, " +
                "dateTime TEXT, " +
                "receiverId INTEGER, " +
                "receiverType TEXT, " +
                "isRead INTEGER DEFAULT 0)");

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

        // ✅ Dữ liệu mẫu
        db.execSQL("INSERT INTO " + TBL_USERS + " (fullName, phoneNumber, password, role, isActive) VALUES " +
                "('Nguyễn Xuân Chiến', '0836663285', 'chien2003', 'user', 1)," +
                "('Đỗ Văn Mạnh', '0987654321', '0987654321', 'owner', 1)," +
                "('Admin', '0999999999', 'adminpass', 'admin', 1)," +
                "('Trần Thị Lan', '0911222333', 'lanpass', 'owner', 1)");

        db.execSQL("INSERT INTO " + TBL_OWNERS + " (id, fullName, phoneNumber, password, role) VALUES " +
                "(4, 'Trần Thị Lan', '0911222333', 'lanpass', 'owner')," +
                "(2, 'Đỗ Văn Mạnh', '0987654321', '0987654321', 'owner')");

        db.execSQL("INSERT INTO " + TBL_PITCHES + " (ownerId, name, price, address, phoneNumber, openTime, closeTime, imageUrl) VALUES " +
                "(2, 'Sân Bóng Đá Bao Cấp', 500000, 'Hoa Lac Hi-tech Park, Tân Xá, Hà Nội', '0979504194', '06:00', '22:00', 'https://afd.com.vn/images/image/tin/co-san-bong.jpg')," +
                "(4, 'Sân Bóng Đại Học Quốc Gia', 550000, 'Tuyến đường Việt Nhật, Thạch Hoà, Hà Nội, 13100', '0961150113', '07:00', '21:00', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQA_gd_yXrzRrWWhzxAKf-XKPWdzayju_O7ig&s.jpg')");

        db.execSQL("INSERT INTO " + TBL_NOTIFICATIONS + " (message, dateTime, receiverId, receiverType, isRead) VALUES " +
                "('Đặt sân thành công', '12:02, 10/07/2025', 1, 'user', 0)," +
                "('Xác nhận thanh toán', '12:03, 10/07/2025', 1, 'user', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 8) {
            db.execSQL("ALTER TABLE " + TBL_NOTIFICATIONS + " ADD COLUMN isRead INTEGER DEFAULT 0");
        }
    }

    public Context getContext() {
        return context;
    }
}
