package com.example.prm392_gr5.Data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Pitch;

import java.util.ArrayList;
import java.util.List;

public class PitchRepository {
    private final DatabaseHelper dbHelper;

    public PitchRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    //=== CRUD Pitches ===//

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
