package com.example.prm392_gr5.Data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.prm392_gr5.Data.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pitch_booking.db";
    private static final int DB_VERSION = 7;

    public static final String TBL_USERS = "users";
    public static final String TBL_PITCHES = "pitches";
    public static final String TBL_SERVICES = "services";
    public static final String TBL_BOOKING = "bookings";
    public static final String TBL_PAYMENTS = "payments";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. users
        db.execSQL("CREATE TABLE " + TBL_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fullName TEXT NOT NULL," +
                "phoneNumber TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "isActive INTEGER DEFAULT 1" +
                ")");

        // 2. pitches
        db.execSQL("CREATE TABLE " + TBL_PITCHES + " (" +
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
                "FOREIGN KEY(ownerId) REFERENCES " + TBL_USERS + "(id)" +
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
                "services TEXT, " +
                "depositAmount REAL NOT NULL, " +
                "status TEXT NOT NULL, " +
                "FOREIGN KEY(userId) REFERENCES " + TBL_USERS + "(id), " +
                "FOREIGN KEY(pitchId) REFERENCES " + TBL_PITCHES + "(id)" +
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

        // 6. messages
        db.execSQL("CREATE TABLE messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender TEXT NOT NULL," +
                "message TEXT NOT NULL," +
                "time TEXT NOT NULL," +
                "receiverType TEXT NOT NULL" +
                ")");

        // Sample users
        db.execSQL("INSERT INTO " + TBL_USERS + " (fullName, phoneNumber, password, role) VALUES " +
                "('Nguyễn Xuân Chiến', '0836663285', 'chien2003', 'user')," +
                "('Đỗ Văn Mạnh', '0987654321', 'ownerpass', 'owner')," +
                "('Admin', '0999999999', 'adminpass', 'admin')");

        // Sample pitches
        db.execSQL("INSERT INTO " + TBL_PITCHES +
                " (ownerId, name, price, address, phoneNumber, openTime, closeTime, imageUrl) VALUES " +
                "(1, 'Sân Bóng Đá Trường Chinh', 500000, '23 Trường Chinh, Đống Đa, Hà Nội', '0243567890', '06:00', '22:00', 'https://cdn-images.vtv.vn/thumb_w/640/2022/12/25/photo-1-1671937473897614575516.jpeg')," +
                "(2, 'Sân Thăng Long', 550000, '45 Phố Huế, Hai Bà Trưng, Hà Nội', '0243987123', '07:00', '21:00', 'https://cdn-images.vtv.vn/thumb_w/640/2022/12/25/photo-1-1671937473897614575516.jpeg')");

        // Sample services
        db.execSQL("INSERT INTO " + TBL_SERVICES + " (pitchId, name, price) VALUES " +
                "(1, 'Thuê bóng', 50000)," +
                "(1, 'Nước uống', 10000)," +
                "(2, 'Thuê bóng', 60000)");

        // Sample bookings
        db.execSQL("INSERT INTO " + TBL_BOOKING + " (userId, pitchId, dateTime, services, depositAmount, status) VALUES " +
                "(1, 1, '2025-06-30T10:00:00', '[1,2]', 20000, 'pending')," +
                "(1, 2, '2025-07-01T12:00:00', '[3]', 30000, 'confirmed')," +
                "(1, 1, '2025-07-02T14:00:00', '[1]', 20000, 'confirmed')");

        // Sample payments
        db.execSQL("INSERT INTO " + TBL_PAYMENTS + " (bookingId, method, amount, status, createdAt) VALUES " +
                "(1, 'VNPay', 20000, 'completed', datetime('now'))," +
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

        // Sample messages
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        String currentTime = sdf.format(new java.util.Date());
        db.execSQL("INSERT INTO messages (sender, message, time, receiverType) VALUES " +
                "('Hệ thống', 'Đã đặt sân thành công lúc 13:00', '" + currentTime + "', 'user')," +
                "('Sân Châu Đường', 'Vui lòng xác nhận trước 14:00', '" + currentTime + "', 'user')," +
                "('Người dùng A', 'Tôi muốn đặt sân lúc 15:00', '" + currentTime + "', 'admin')," +
                "('Người dùng B', 'Vui lòng kiểm tra trạng thái sân', '" + currentTime + "', 'admin')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_BOOKING);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_PITCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_USERS);
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sender", message.getSender());
        values.put("message", message.getMessage());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        String currentTime = sdf.format(new java.util.Date());
        values.put("time", currentTime);
        values.put("receiverType", message.getReceiverType());
        db.insert("messages", null, values);
        db.close();
    }

    public List<Message> getMessages(String receiverType) {
        List<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM messages WHERE receiverType = ?", new String[]{receiverType});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                messageList.add(new Message(id, sender, message, time, receiverType));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messageList;
    }
}