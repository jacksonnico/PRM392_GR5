package com.example.prm392_gr5.Data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "pitch_booking.db";
    private static final int    DB_VERSION = 8;


    public static final String TBL_USERS    = "users";
    public static final String TBL_PITCHES  = "pitches";
    public static final String TBL_SERVICES = "services";
    public static final String TBL_BOOKING  = "bookings";
    public static final String TBL_PAYMENTS = "payments";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. users

        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fullName TEXT NOT NULL," +
                "phoneNumber TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "isActive INTEGER DEFAULT 1" +
                ")");


        // 2. pitches
        db.execSQL("CREATE TABLE pitches (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ownerId INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "address TEXT," +
                "phoneNumber TEXT," +
                "openTime TEXT," +
                "closeTime TEXT," +
                "imageUrl TEXT," +
                "status TEXT DEFAULT 'active'," +
                "FOREIGN KEY(ownerId) REFERENCES users(id)" +
                ")");


        // 3. services
        db.execSQL("CREATE TABLE " + TBL_SERVICES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pitchId INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "FOREIGN KEY(pitchId) REFERENCES " + TBL_PITCHES + "(id)" +
                ")");

        // 4. bookings
        db.execSQL("CREATE TABLE " + TBL_BOOKING + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER NOT NULL, " +
                "pitchId INTEGER NOT NULL, " +
                "dateTime TEXT NOT NULL, " +
                "timeSlot TEXT NOT NULL, " +      // khung giờ như \"08:00-10:00\"
                "services TEXT, " +
                "status TEXT NOT NULL, " +
                "FOREIGN KEY(userId) REFERENCES users(id), " +
                "FOREIGN KEY(pitchId) REFERENCES pitches(id)" +
                ")");

        // 5. payments
        db.execSQL("CREATE TABLE " + TBL_PAYMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bookingId INTEGER NOT NULL, " +
                "method TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "status TEXT NOT NULL, " +
                "createdAt TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(bookingId) REFERENCES " + TBL_BOOKING + "(id)" +
                ")");

        // Sample users
        db.execSQL("INSERT INTO users (fullName, phoneNumber, password, role) VALUES " +
                "('Nguyễn Xuân Chiến', '0836663285', 'chien2003', 'user')," +
                "('Đỗ Văn Mạnh', '0987654321', 'ownerpass', 'owner')," +
                "('Admin', '0999999999', 'adminpass', 'admin')");//comment

        db.execSQL("INSERT INTO " + TBL_PITCHES +
                " (ownerId,name,price,address,phoneNumber,openTime,closeTime,imageUrl) VALUES" +
                "(1, 'Sân Bóng Đá Trường Chinh', 500000, '23 Trường Chinh, Đống Đa, Hà Nội', '0243567890', '06:00', '22:00', 'https://cdn-images.vtv.vn/thumb_w/640/2022/12/25/photo-1-1671937473897614575516.jpeg')," +
                "(2, 'Sân Thăng Long', 550000, '45 Phố Huế, Hai Bà Trưng, Hà Nội', '0243987123', '07:00', '21:00', 'https://cdn-images.vtv.vn/thumb_w/640/2022/12/25/photo-1-1671937473897614575516.jpeg') "
        );
        // Sample services
        db.execSQL("INSERT INTO services (pitchId, name, price) VALUES (1, 'Thuê bóng', 50000)");
        db.execSQL("INSERT INTO services (pitchId, name, price) VALUES (1, 'Nước uống', 10000)");
        db.execSQL("INSERT INTO services (pitchId, name, price) VALUES (2, 'Thuê bóng', 60000)");



        // Sample payments
        db.execSQL("INSERT INTO payments (bookingId, method, amount, status) VALUES (1, 'VNPay', 20000, 'completed')");
        db.execSQL("INSERT INTO payments (bookingId, method, amount, status, createdAt) VALUES " +
                "(1, 'VNPay', 30000, 'completed', datetime('now', '-1 day'))," +
                "(1, 'VNPay', 40000, 'completed', datetime('now', '-2 day'))," +
                "(1, 'Momo', 50000, 'completed', date('now', '-10 day'))," +
                "(1, 'Momo', 60000, 'completed', date('now', '-15 day'))," +
                "(1, 'VNPay', 70000, 'completed', '2025-05-01')," +
                "(1, 'VNPay', 80000, 'completed', '2025-04-15')," +
                "(1, 'VNPay', 90000, 'completed', '2025-01-10')," +
                "(1, 'Momo', 100000, 'completed', '2025-02-20')," +
                "(1, 'VNPay', 110000, 'completed', '2024-12-31')," +
                "(1, 'VNPay', 120000, 'completed', '2023-11-20')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop & recreate
        db.execSQL("DROP TABLE IF EXISTS " + TBL_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_BOOKING);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_PITCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_USERS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}



