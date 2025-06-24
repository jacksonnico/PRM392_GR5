package com.example.prm392_gr5.Data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Intent;
import android.net.Uri;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pitch_booking.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fullName TEXT NOT NULL," +
                "phoneNumber TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL" +
                ")");

        // Pitches
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
                "FOREIGN KEY(ownerId) REFERENCES users(id)" +
                ")");

        // Services
        db.execSQL("CREATE TABLE services (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pitchId INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "FOREIGN KEY(pitchId) REFERENCES pitches(id)" +
                ")");

        // Bookings
        db.execSQL("CREATE TABLE bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER NOT NULL," +
                "pitchId INTEGER NOT NULL," +
                "dateTime TEXT NOT NULL," +
                "services TEXT," +
                "depositAmount REAL NOT NULL," +
                "status TEXT NOT NULL," +
                "FOREIGN KEY(userId) REFERENCES users(id)," +
                "FOREIGN KEY(pitchId) REFERENCES pitches(id)" +
                ")");

        // Payments
        db.execSQL("CREATE TABLE payments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bookingId INTEGER NOT NULL," +
                "method TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "status TEXT NOT NULL," +
                "createdAt TEXT DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(bookingId) REFERENCES bookings(id)" +
                ")");


        // Users
        db.execSQL("INSERT INTO users (fullName, phoneNumber, password, role) VALUES ('Nguyễn Xuân Chiến', '0836663285', 'chien2003', 'user')");
        db.execSQL("INSERT INTO users (fullName, phoneNumber, password, role) VALUES ('Đỗ Văn Mạnh', '0987654321', 'ownerpass', 'owner')");
        db.execSQL("INSERT INTO users (fullName, phoneNumber, password, role) VALUES ('Admin', '0999999999', 'adminpass', 'admin')");

        // Pitches
        db.execSQL("INSERT INTO pitches (ownerId, name, price, address, phoneNumber, openTime, closeTime, imageUrl) " +
                "VALUES (2, 'Sân bao cấp', 500000, '123 Đường A, Q.1', '0281234567', '07:00', '22:00', 'https://pos.nvncdn.com/b0b717-26181/art/artCT/20240812_0rmC0gAF.jpg')");
        db.execSQL("INSERT INTO pitches (ownerId, name, price, address, phoneNumber, openTime, closeTime, imageUrl) " +
                "VALUES (2, 'Sân đại học quốc gia Hà Nội', 500000, '456 Đường B, Q.9', '0287654321', '06:00', '23:00', 'https://pos.nvncdn.com/b0b717-26181/art/artCT/20240812_0rmC0gAF.jpg')");

        // Services
        db.execSQL("INSERT INTO services (pitchId, name, price) VALUES (1, 'Thuê bóng', 50000)");
        db.execSQL("INSERT INTO services (pitchId, name, price) VALUES (1, 'Nước uống', 10000)");
        db.execSQL("INSERT INTO services (pitchId, name, price) VALUES (2, 'Thuê bóng', 60000)");

        // Bookings
        db.execSQL("INSERT INTO bookings (userId, pitchId, dateTime, services, depositAmount, status) " +
                "VALUES (1, 1, '2025-06-30T10:00:00', '[1,2]', 20000, 'pending')");

        // Payments
        db.execSQL("INSERT INTO payments (bookingId, method, amount, status) " +
                "VALUES (1, 'VNPay', 20000, 'completed')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS payments");
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS services");
        db.execSQL("DROP TABLE IF EXISTS pitches");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }


//    public static void openMap(Context context, String address) {
//        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
//            context.startActivity(mapIntent);
//        }
//    }
}