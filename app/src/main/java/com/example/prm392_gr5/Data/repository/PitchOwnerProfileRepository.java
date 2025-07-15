package com.example.prm392_gr5.Data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.prm392_gr5.Data.db.DatabaseHelper;

public class PitchOwnerProfileRepository {
    private final DatabaseHelper dbHelper;

    public PitchOwnerProfileRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public String getFullNameFromOwnerId(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT fullName FROM " + DatabaseHelper.TBL_OWNERS + " WHERE id = ?",
                    new String[]{String.valueOf(ownerId)});
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
            }
            return "Chủ sân";
        } catch (Exception e) {
            Log.e("PitchOwnerProfileRepository", "Error getting owner name for id " + ownerId + ": " + e.getMessage());
            return "Chủ sân";
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}