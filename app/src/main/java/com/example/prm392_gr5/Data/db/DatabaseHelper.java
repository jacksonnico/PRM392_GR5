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
                "(1, 'Sân Bóng Đá Bao Cấp', 500000, 'Hoa Lac Hi-tech Park, Tân Xá, Hà Nội', '0979504194', '06:00', '22:00', 'https://afd.com.vn/images/image/tin/co-san-bong.jpg')," +
                "(2, 'Sân Bóng Đại Học Quốc Gia', 550000, 'Tuyến đường Việt Nhật, Thạch Hoà, Hà Nội, 13100', '0961150113', '07:00', '21:00', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQA_gd_yXrzRrWWhzxAKf-XKPWdzayju_O7ig&s.jpg'), "+
                "(3, 'Sân Bóng 5 Cửa Ô', 500000, '2GHX+425, Tân Xá, Thạch Thất, Hà Nội', '0979504194', '06:00', '22:00', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCcMUCBLWw2FVmuRYVJbkQc2TvZ5lVGwTHEQ&s.jpg')"

        );
        // Sample services
        db.execSQL("INSERT INTO services (pitchId, name, price) VALUES (1, 'Thuê bóng', 50000)");
        db.execSQL("INSERT INTO services (pitchId, name, price) VALUES (1, 'Nước uống', 10000)");
        db.execSQL("INSERT INTO services (pitchId, name, price) VALUES (2, 'Thuê bóng', 60000)");




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



