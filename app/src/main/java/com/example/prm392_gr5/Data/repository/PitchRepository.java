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

    public PitchRepository(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);//comment
    }

    public int getPitchCountByOwner(int ownerId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM pitches WHERE ownerId = ?",
                    new String[]{String.valueOf(ownerId)});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    public List<Pitch> getPitchesByOwner(int ownerId) {
        List<Pitch> pitches = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("pitches", null, "ownerId = ?", new String[]{String.valueOf(ownerId)},
                    null, null, "name ASC");
            while (cursor.moveToNext()) {
                Pitch pitch = extractPitchFromCursor(cursor);
                pitches.add(pitch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return pitches;
    }

    public List<Pitch> getAllPitches() {
        List<Pitch> pitches = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("pitches", null, null, null, null, null, "name ASC");
            while (cursor.moveToNext()) {
                Pitch pitch = new Pitch();
                pitch.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                pitch.setOwnerId(cursor.getInt(cursor.getColumnIndexOrThrow("ownerId")));
                pitch.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                pitch.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                pitch.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                pitch.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber")));
                pitch.setOpenTime(cursor.getString(cursor.getColumnIndexOrThrow("openTime")));
                pitch.setCloseTime(cursor.getString(cursor.getColumnIndexOrThrow("closeTime")));
                pitch.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
                pitch.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                pitches.add(pitch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return pitches;
    }


    public Pitch getPitchById(int pitchId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("pitches", null, "id = ?", new String[]{String.valueOf(pitchId)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                return extractPitchFromCursor(cursor);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    public int getTotalPitchCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM pitches", null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    public void suspendPitch(int pitchId) {
        updatePitchStatus(pitchId, "suspended");
    }

    public void activatePitch(int pitchId) {
        updatePitchStatus(pitchId, "active");
    }

    private void updatePitchStatus(int pitchId, String status) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("status", status);
            db.update("pitches", cv, "id = ?", new String[]{String.valueOf(pitchId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }

    public List<Pitch> getPitchesByOwnerPaginated(int ownerId, int page, int pageSize) {
        List<Pitch> pitches = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            int offset = (page - 1) * pageSize;
            cursor = db.query("pitches", null, "ownerId = ?", new String[]{String.valueOf(ownerId)},
                    null, null, "name ASC", offset + "," + pageSize);
            while (cursor.moveToNext()) {
                Pitch pitch = extractPitchFromCursor(cursor);
                pitches.add(pitch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return pitches;
    }

    public String getOwnerNameById(int ownerId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT fullName FROM users WHERE id = ?", new String[]{String.valueOf(ownerId)});
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return "Không rõ";
    }


    private Pitch extractPitchFromCursor(Cursor cursor) {
        Pitch pitch = new Pitch();
        pitch.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        pitch.setOwnerId(cursor.getInt(cursor.getColumnIndexOrThrow("ownerId")));
        pitch.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        pitch.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
        pitch.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        pitch.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber")));
        pitch.setOpenTime(cursor.getString(cursor.getColumnIndexOrThrow("openTime")));
        pitch.setCloseTime(cursor.getString(cursor.getColumnIndexOrThrow("closeTime")));
        pitch.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
        pitch.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        return pitch;
    }

    // THÊM MỚI SÂN
    public long addPitch(Pitch pitch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ownerId", pitch.getOwnerId());
        values.put("name", pitch.getName());
        values.put("price", pitch.getPrice());
        values.put("address", pitch.getAddress());
        values.put("phoneNumber", pitch.getPhoneNumber());
        values.put("openTime", pitch.getOpenTime());
        values.put("closeTime", pitch.getCloseTime());
        values.put("imageUrl", pitch.getImageUrl());
        values.put("status", pitch.getStatus());
        long result = db.insert("pitches", null, values);
        db.close();
        return result;
    }

    // CẬP NHẬT SÂN
    public int updatePitch(Pitch pitch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", pitch.getName());
        values.put("price", pitch.getPrice());
        values.put("address", pitch.getAddress());
        values.put("phoneNumber", pitch.getPhoneNumber());
        values.put("openTime", pitch.getOpenTime());
        values.put("closeTime", pitch.getCloseTime());
        values.put("imageUrl", pitch.getImageUrl());
        values.put("status", pitch.getStatus());
        values.put("ownerId", pitch.getOwnerId());

        int result = db.update("pitches", values, "id = ?", new String[]{String.valueOf(pitch.getId())});
        db.close();
        return result;
    }
    public void suspendPitchesByOwner(int ownerId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("status", "suspended");
            db.update("pitches", cv, "ownerId = ?", new String[]{String.valueOf(ownerId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }
    public void activatePitchesByOwner(int ownerId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", "active");
        db.update("pitches", cv, "ownerId = ?", new String[]{String.valueOf(ownerId)});
        db.close();
    }


    // XÓA SÂN
    public int deletePitch(int pitchId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("pitches", "id = ?", new String[]{String.valueOf(pitchId)});
        db.close();
        return result;
    }

}
