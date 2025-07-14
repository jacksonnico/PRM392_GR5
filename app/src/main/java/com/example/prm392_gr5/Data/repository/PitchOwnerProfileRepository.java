package com.example.prm392_gr5.Data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;

public class PitchOwnerProfileRepository {
    private final DatabaseHelper dbHelper;

    public PitchOwnerProfileRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Lấy tên đầy đủ của owner từ ownerId
     */
    public String getFullNameFromOwnerId(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT fullName FROM " + DatabaseHelper.TBL_OWNERS + " WHERE id = ?",
                    new String[]{String.valueOf(ownerId)}
            );
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
            }
            return "Owner_" + ownerId;
        } catch (Exception e) {
            e.printStackTrace();
            return "Owner_" + ownerId;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    /**
     * Lấy ownerId từ pitchName
     */
    public int getOwnerIdByPitchName(String pitchName) {
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
            return 0; // Owner không tìm thấy
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}
