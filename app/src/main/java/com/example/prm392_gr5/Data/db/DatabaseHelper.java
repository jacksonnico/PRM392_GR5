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
    private static final int DATABASE_VERSION = 5;

    public static final String TBL_USERS = "users";
    public static final String TBL_OWNERS = "owners";
    public static final String TBL_MESSAGES = "messages";
    public static final String TBL_PITCHES = "pitches";
    public static final String TBL_BOOKINGS = "bookings";
    public static final String TBL_NOTIFICATIONS = "notifications";
    public static final String TBL_PAYMENTS = "payments";
    public static final String TBL_SERVICES = "services";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        db.execSQL("INSERT INTO " + TBL_USERS + " (fullName, phoneNumber, password, role, isActive) VALUES " +
                "('Nguyễn Xuân Chiến', '0836663285', 'chien2003', 'user', 1)," +
                "('Đỗ Văn Mạnh', '0987654321', '0987654321', 'owner', 1)," +
                "('Admin', '0999999999', 'adminpass', 'admin', 1)");

        db.execSQL("INSERT INTO " + TBL_OWNERS + " (fullName, phoneNumber, password, role) VALUES " +
                "('Đỗ Văn Mạnh', '0987654321', 'ownerpass', 'owner')");

        db.execSQL("INSERT INTO " + TBL_PITCHES + " (ownerId, name, price, address, phoneNumber, openTime, closeTime, imageUrl) VALUES " +
                "(1, 'Sân Bóng Đá Bao Cấp', 500000, 'Hoa Lac Hi-tech Park, Tân Xá, Hà Nội', '0979504194', '06:00', '22:00', 'https://afd.com.vn/images/image/tin/co-san-bong.jpg')," +
                "(2, 'Sân Bóng Đại Học Quốc Gia', 550000, 'Tuyến đường Việt Nhật, Thạch Hoà, Hà Nội, 13100', '0961150113', '07:00', '21:00', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQA_gd_yXrzRrWWhzxAKf-XKPWdzayju_O7ig&s.jpg')," +
                "(2, 'Sân Bóng 5 Cửa Ô', 500000, '2GHX+425, Tân Xá, Thạch Thất, Hà Nội', '0979504194', '06:00', '22:00', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCcMUCBLWw2FVmuRYVJbkQc2TvZ5lVGwTHEQ&s.jpg')");

        db.execSQL("INSERT INTO " + TBL_MESSAGES + " (sender, message, time, pitchName, userId) VALUES " +
                "('1', 'Đặt sân lúc 10h', '12:00, 10/07/2025', 'Sân Bóng Đá Trường Chinh', 1)," +
                "('0', 'Đã nhận, xác nhận nhé!', '12:01, 10/07/2025', 'Sân Bóng Đá Trường Chinh', 0)");

        db.execSQL("INSERT INTO " + TBL_BOOKINGS + " (pitchId, userId, dateTime, status, depositAmount, services) VALUES " +
                "(1, 1, '2025-07|10T10:00:00', 'confirmed', 200000.0, '')");

        db.execSQL("INSERT INTO " + TBL_SERVICES + " (pitchId, name, price) VALUES " +
                "(1, 'Thuê bóng', 50000)," +
                "(1, 'Nước uống', 10000)," +
                "(2, 'Thuê bóng', 60000)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TBL_BOOKINGS + " ADD COLUMN depositAmount REAL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TBL_BOOKINGS + " ADD COLUMN services TEXT");
            db.execSQL("ALTER TABLE " + TBL_BOOKINGS + " ADD COLUMN timeSlot TEXT");
        }
    }

    // Các phương thức còn lại giữ nguyên
    public String getFullNameFromUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fullName = "User_" + userId;
        Cursor cursor = db.rawQuery("SELECT fullName FROM " + TBL_USERS + " WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
        }
        cursor.close();
        return fullName;
    }

    public String getPhoneNumberFromUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String phone = "";
        Cursor cursor = db.rawQuery("SELECT phoneNumber FROM " + TBL_USERS + " WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            phone = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"));
        }
        cursor.close();
        return phone;
    }

    public String getFullNameFromOwnerId(int ownerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fullName = "Owner_" + ownerId;
        Cursor cursor = db.rawQuery("SELECT fullName FROM " + TBL_OWNERS + " WHERE id = ?", new String[]{String.valueOf(ownerId)});
        if (cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
        }
        cursor.close();
        return fullName;
    }

    public void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sender", String.valueOf(message.getUserId()));
        values.put("message", message.getMessage());
        values.put("time", message.getTime());
        values.put("pitchName", message.getPitchName());
        values.put("userId", message.getUserId());
        db.insert(TBL_MESSAGES, null, values);
        db.close();
    }

    public List<Message> getMessagesByPitch(String pitchName) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TBL_MESSAGES + " WHERE pitchName = ? ORDER BY id ASC", new String[]{pitchName});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String msg = cursor.getString(cursor.getColumnIndexOrThrow("message"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
            String displayName = userId == 0 ? getFullNameFromOwnerId(2) : getFullNameFromUserId(userId);
            messages.add(new Message(id, displayName, msg, time, pitchName, userId));
        }
        cursor.close();
        db.close();
        return messages;
    }

    public String getUserNameAndPhone(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("SELECT fullName, phoneNumber FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"));
            result = name + " - " + phone;
        }
        cursor.close();
        return result;
    }

    public void addNotification(String message, String dateTime, int receiverId, String receiverType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("message", message);
        values.put("dateTime", dateTime);
        values.put("receiverId", receiverId);
        values.put("receiverType", receiverType);
        db.insert(TBL_NOTIFICATIONS, null, values);
        db.close();
    }

    public List<Notification> getNotifications(int receiverId, String receiverType) {
        List<Notification> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notifications WHERE receiverId = ? AND receiverType = ? ORDER BY id DESC",
                new String[]{String.valueOf(receiverId), receiverType});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String msg = cursor.getString(1);
            String time = cursor.getString(2);
            list.add(new Notification(id, msg, time, receiverId, receiverType));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Notification> getNotificationsForOwner() {
        List<Notification> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT n.*, u.fullName AS userName FROM notifications n " +
                "JOIN users u ON n.receiverId = u.id " +
                "WHERE n.receiverType = 'user' ORDER BY n.id DESC";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
            String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
            String userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"));

            Notification notification = new Notification(id, message, dateTime, 0, "user");
            notification.setUserName(userName);
            list.add(notification);
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<UserMessageSummary> getUserMessageSummariesForOwner(int ownerId) {
        List<UserMessageSummary> summaries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor pitchCursor = db.rawQuery("SELECT name FROM " + TBL_PITCHES + " WHERE ownerId = ?", new String[]{String.valueOf(ownerId)});
        if (pitchCursor.moveToFirst()) {
            do {
                String pitchName = pitchCursor.getString(pitchCursor.getColumnIndexOrThrow("name"));
                Cursor messageCursor = db.rawQuery("SELECT sender, message, time, userId FROM " + TBL_MESSAGES + " WHERE pitchName = ? ORDER BY id DESC LIMIT 1", new String[]{pitchName});
                if (messageCursor.moveToFirst()) {
                    int userId = messageCursor.getInt(messageCursor.getColumnIndexOrThrow("userId"));
                    String lastMessage = messageCursor.getString(messageCursor.getColumnIndexOrThrow("message"));
                    String lastTime = messageCursor.getString(messageCursor.getColumnIndexOrThrow("time"));
                    String displayName = getFullNameFromUserId(userId);
                    String phone = getPhoneNumberFromUserId(userId);
                    summaries.add(new UserMessageSummary(displayName, lastMessage, lastTime, pitchName, phone));
                }
                messageCursor.close();
            } while (pitchCursor.moveToNext());
        }
        pitchCursor.close();
        db.close();
        return summaries;
    }
}