package com.example.prm392_gr5.Data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log; // Thêm Log để debug

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.model.ScheduleInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PitchRepository {
    private final DatabaseHelper dbHelper;
    // Định dạng cho datetime trong DB (yyyy-MM-dd'T'HH:mm:ss)
    private final SimpleDateFormat DB_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    // Định dạng chỉ giờ để hiển thị khung giờ (HH:mm)
    private final SimpleDateFormat TIME_SLOT_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    // Định dạng ngày cho truy vấn DB (yyyy-MM-dd)
    private final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    public PitchRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    //=== CRUD Pitches ===//

    public boolean insertPitch(int ownerId, String name, String address, double price,
                               String openTime, String closeTime, String phoneNumber, String imageUrl) {
        ContentValues values = new ContentValues();
        values.put("ownerId", ownerId);
        values.put("name", name);
        values.put("address", address);
        values.put("price", price);
        values.put("phoneNumber", phoneNumber);
        values.put("openTime", openTime);
        values.put("closeTime", closeTime);
        values.put("imageUrl", imageUrl);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = db.insert("pitches", null, values);
        db.close();

        return result != -1;
    }
    public long addPitch(Pitch pitch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("ownerId", pitch.getOwnerId());
            cv.put("name", pitch.getName());
            cv.put("price", pitch.getPrice());
            cv.put("address", pitch.getAddress());
            cv.put("phoneNumber", pitch.getPhoneNumber());
            cv.put("openTime", pitch.getOpenTime());
            cv.put("closeTime", pitch.getCloseTime());
            cv.put("imageUrl", pitch.getImageUrl());
            cv.put("status", pitch.getStatus());
            return db.insert(DatabaseHelper.TBL_PITCHES, null, cv);
        } finally {
            db.close();
        }
    }

    public Pitch getPitchById(int pitchId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(
                    DatabaseHelper.TBL_PITCHES,
                    null,
                    "id = ?",
                    new String[]{ String.valueOf(pitchId) },
                    null, null, null
            );
            if (c.moveToFirst()) {
                return extractPitchFromCursor(c);
            }
            return null;
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    public List<Pitch> getAllPitches() {
        List<Pitch> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(
                    DatabaseHelper.TBL_PITCHES,
                    null, null, null, null, null,
                    "name ASC"
            );
            while (c.moveToNext()) {
                list.add(extractPitchFromCursor(c));
            }
            return list;
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    public int updatePitch(Pitch pitch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("ownerId", pitch.getOwnerId());
            cv.put("name", pitch.getName());
            cv.put("price", pitch.getPrice());
            cv.put("address", pitch.getAddress());
            cv.put("phoneNumber", pitch.getPhoneNumber());
            cv.put("openTime", pitch.getOpenTime());
            cv.put("closeTime", pitch.getCloseTime());
            cv.put("imageUrl", pitch.getImageUrl());
            cv.put("status", pitch.getStatus());
            return db.update(
                    DatabaseHelper.TBL_PITCHES,
                    cv,
                    "id = ?",
                    new String[]{ String.valueOf(pitch.getId()) }
            );
        } finally {
            db.close();
        }
    }

    public int deletePitch(int pitchId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            return db.delete(
                    DatabaseHelper.TBL_PITCHES,
                    "id = ?",
                    new String[]{ String.valueOf(pitchId) }
            );
        } finally {
            db.close();
        }
    }

    //=== Thống kê ===//

    public int getTotalPitchCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TBL_PITCHES, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    public int getPitchCountByOwner(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT COUNT(*) FROM " + DatabaseHelper.TBL_PITCHES + " WHERE ownerId = ?",
                    new String[]{ String.valueOf(ownerId) }
            );
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    //=== Truy vấn theo owner ===//

    public List<Pitch> getPitchesByOwner(int ownerId) {
        List<Pitch> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(
                    DatabaseHelper.TBL_PITCHES,
                    null,
                    "ownerId = ?",
                    new String[]{ String.valueOf(ownerId) },
                    null, null,
                    "name ASC"
            );
            while (c.moveToNext()) {
                list.add(extractPitchFromCursor(c));
            }
            return list;
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    public List<Pitch> getPitchesByOwnerId(int ownerId) {
        List<Pitch> pitches = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM pitches WHERE ownerId = ?", new String[]{String.valueOf(ownerId)});
        while (cursor.moveToNext()) {
            Pitch pitch = new Pitch();
            pitch.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            pitch.setOwnerId(ownerId);
            pitch.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            pitch.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            pitch.setOpenTime(cursor.getString(cursor.getColumnIndexOrThrow("openTime")));
            pitch.setCloseTime(cursor.getString(cursor.getColumnIndexOrThrow("closeTime")));
            pitch.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber")));
            pitch.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
            pitch.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            pitches.add(pitch);
        }

        cursor.close();
        db.close();
        return pitches;
    }
    public List<Pitch> getPitchesByOwnerPaginated(int ownerId, int page, int pageSize) {
        List<Pitch> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            int offset = (page - 1) * pageSize;
            c = db.query(
                    DatabaseHelper.TBL_PITCHES,
                    null,
                    "ownerId = ?",
                    new String[]{ String.valueOf(ownerId) },
                    null, null,
                    "name ASC",
                    offset + "," + pageSize
            );
            while (c.moveToNext()) {
                list.add(extractPitchFromCursor(c));
            }
            return list;
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    //=== Kích hoạt / Đình chỉ ===//

    public void suspendPitch(int pitchId) {
        updatePitchStatus(pitchId, "suspended");
    }

    public void activatePitch(int pitchId) {
        updatePitchStatus(pitchId, "active");
    }

    public void suspendPitchesByOwner(int ownerId) {
        bulkUpdateStatus("suspended", "ownerId = ?", new String[]{ String.valueOf(ownerId) });
    }

    public void activatePitchesByOwner(int ownerId) {
        bulkUpdateStatus("active", "ownerId = ?", new String[]{ String.valueOf(ownerId) });
    }

    private void updatePitchStatus(int pitchId, String status) {
        bulkUpdateStatus(status, "id = ?", new String[]{ String.valueOf(pitchId) });
    }

    private void bulkUpdateStatus(String status, String where, String[] args) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("status", status);
            db.update(DatabaseHelper.TBL_PITCHES, cv, where, args);
        } finally {
            db.close();
        }
    }

    //=== Truy vấn chủ sở hữu ===//

    public String getOwnerNameById(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT fullName FROM users WHERE id = ?",
                    new String[]{ String.valueOf(ownerId) }
            );
            if (c.moveToFirst()) {
                return c.getString(0);
            }
            return "Không rõ";
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    //=== Helpers ===//
    public List<ScheduleInfo> getScheduleForPitch(int pitchId, Calendar selectedDate, Pitch pitch) {
        List<ScheduleInfo> scheduleList = generateTimeSlots(pitch);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectedDateStr = DB_DATE_FORMAT.format(selectedDate.getTime());
        Log.d("PitchRepository", "Querying schedule for Pitch ID: " + pitchId + " on Date: " + selectedDateStr);


        // Chú ý: DATE(b.dateTime) chỉ lấy phần ngày, điều này hoạt động tốt
        String query = "SELECT b.id, b.dateTime, b.status, u.fullName, u.phoneNumber " +
                "FROM bookings b JOIN users u ON b.userId = u.id " +
                "WHERE b.pitchId = ? AND DATE(b.dateTime) = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(pitchId), selectedDateStr});
        Log.d("PitchRepository", "Bookings found: " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                String dateTimeFromDb = cursor.getString(cursor.getColumnIndexOrThrow("dateTime"));
                Log.d("PitchRepository", "Booking DateTime from DB: " + dateTimeFromDb);

                Date bookingStartDateTime = null;
                try {
                    // Cố gắng parse theo định dạng chuẩn yyyy-MM-dd'T'HH:mm:ss
                    bookingStartDateTime = DB_DATETIME_FORMAT.parse(dateTimeFromDb);
                } catch (ParseException e) {
                    Log.e("PitchRepository", "Error parsing booking dateTime: " + dateTimeFromDb + ". " + e.getMessage());
                    // Log the error, but continue to process valid entries.
                    // If you encounter "HH:mm-HH:mm" here, it will fail.
                    // You MUST fix your database data.
                    continue; // Skip this malformed entry
                }

                String bookingTimeSlotStr = TIME_SLOT_FORMAT.format(bookingStartDateTime); // ví dụ "06:00"

                for (ScheduleInfo schedule : scheduleList) {
                    if (schedule.timeSlot.startsWith(bookingTimeSlotStr)) {
                        schedule.isBooked = true;
                        schedule.customerName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
                        schedule.customerPhone = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"));
                        schedule.status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                        schedule.bookingId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        schedule.bookingDate = selectedDate.getTime(); // ✅ rất quan trọng để adapter so sánh ngày
                        break;
                    }
                }

            }
        } finally {
            cursor.close();
            db.close();
        }

        return scheduleList;
    }

    private List<ScheduleInfo> generateTimeSlots(Pitch pitch) {
        List<ScheduleInfo> timeSlots = new ArrayList<>();
        SimpleDateFormat hourMinuteFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date openTime = hourMinuteFormat.parse(pitch.getOpenTime());
            Date closeTime = hourMinuteFormat.parse(pitch.getCloseTime());

            Calendar slotCal = Calendar.getInstance();
            Calendar openCal = Calendar.getInstance();
            Calendar closeCal = Calendar.getInstance();

            openCal.setTime(openTime);
            closeCal.setTime(closeTime);

            slotCal.set(Calendar.HOUR_OF_DAY, openCal.get(Calendar.HOUR_OF_DAY));
            slotCal.set(Calendar.MINUTE, openCal.get(Calendar.MINUTE));
            slotCal.set(Calendar.SECOND, 0);
            slotCal.set(Calendar.MILLISECOND, 0);

            Calendar endTimeForLoop = Calendar.getInstance();
            endTimeForLoop.set(Calendar.HOUR_OF_DAY, closeCal.get(Calendar.HOUR_OF_DAY));
            endTimeForLoop.set(Calendar.MINUTE, closeCal.get(Calendar.MINUTE));
            endTimeForLoop.set(Calendar.SECOND, 0);
            endTimeForLoop.set(Calendar.MILLISECOND, 0);

            while (slotCal.before(endTimeForLoop)) {
                Date start = slotCal.getTime();
                slotCal.add(Calendar.HOUR_OF_DAY, 2); // 2 tiếng 1 slot
                Date end = slotCal.getTime();

                ScheduleInfo schedule = new ScheduleInfo();
                schedule.timeSlot = TIME_SLOT_FORMAT.format(start) + "-" + TIME_SLOT_FORMAT.format(end);
                schedule.isBooked = false;
                timeSlots.add(schedule);
            }

        } catch (ParseException e) {
            Log.e("PitchRepository", "Lỗi khi tạo khung giờ: " + e.getMessage());
            return generateDefaultTimeSlots(); // fallback
        }

        return timeSlots;
    }


    // Fallback method to generate default time slots
    private List<ScheduleInfo> generateDefaultTimeSlots() {
        List<ScheduleInfo> timeSlots = new ArrayList<>();
        Calendar slot = Calendar.getInstance();
        slot.set(Calendar.HOUR_OF_DAY, 8);
        slot.set(Calendar.MINUTE, 0);
        slot.set(Calendar.SECOND, 0);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 22);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);

        while (slot.before(end)) {
            ScheduleInfo schedule = new ScheduleInfo();
            schedule.timeSlot = TIME_SLOT_FORMAT.format(slot.getTime());
            schedule.isBooked = false;
            timeSlots.add(schedule);
            slot.add(Calendar.HOUR_OF_DAY, 1); // 1 hour intervals
        }
        return timeSlots;
    }
    public int getOwnerIdFromPitchName(String pitchName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT ownerId FROM " + DatabaseHelper.TBL_PITCHES + " WHERE name = ?",
                    new String[]{pitchName}
            );
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow("ownerId"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return -1; // ❌ Không tìm thấy
    }

    private Pitch extractPitchFromCursor(Cursor c) {
        Pitch p = new Pitch();
        p.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        p.setOwnerId(c.getInt(c.getColumnIndexOrThrow("ownerId")));
        p.setName(c.getString(c.getColumnIndexOrThrow("name")));
        p.setPrice(c.getDouble(c.getColumnIndexOrThrow("price")));
        p.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
        p.setPhoneNumber(c.getString(c.getColumnIndexOrThrow("phoneNumber")));
        p.setOpenTime(c.getString(c.getColumnIndexOrThrow("openTime")));
        p.setCloseTime(c.getString(c.getColumnIndexOrThrow("closeTime")));
        p.setImageUrl(c.getString(c.getColumnIndexOrThrow("imageUrl")));
        p.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
        return p;
    }
}