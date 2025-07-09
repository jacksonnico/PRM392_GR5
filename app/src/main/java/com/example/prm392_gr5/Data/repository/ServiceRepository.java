package com.example.prm392_gr5.Data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.model.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceRepository {
    private final DatabaseHelper dbHelper;

    public ServiceRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    public List<Pitch> getAllPitchesByOwner(int ownerId) {
        List<Pitch> pitchList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM pitches WHERE ownerId = ?",
                new String[]{String.valueOf(ownerId)}
        );

        while (cursor.moveToNext()) {
            Pitch pitch = new Pitch();
            pitch.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            pitch.setOwnerId(cursor.getInt(cursor.getColumnIndexOrThrow("ownerId")));
            pitch.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            pitch.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            pitch.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            pitch.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber")));
            pitch.setOpenTime(cursor.getString(cursor.getColumnIndexOrThrow("openTime")));
            pitch.setCloseTime(cursor.getString(cursor.getColumnIndexOrThrow("closeTime")));
            pitch.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
            pitchList.add(pitch);
        }

        cursor.close();
        db.close();

        return pitchList;
    }

    public List<Service> getServicesByPitchId(int pitchId) {
        List<Service> services = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM services WHERE pitchId = ?",
                new String[]{String.valueOf(pitchId)});

        while (cursor.moveToNext()) {
            Service service = new Service();
            service.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            service.setPitchId(cursor.getInt(cursor.getColumnIndexOrThrow("pitchId")));
            service.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            service.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            services.add(service);
        }

        cursor.close();
        db.close();
        return services;
    }

    public boolean insertService(int pitchId, String name, double price) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("pitchId", pitchId);
        values.put("name", name);
        values.put("price", price);

        long result = db.insert("services", null, values);
        db.close();
        return result != -1;
    }

    public boolean updateService(int serviceId, String name, double price) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);

        int result = db.update("services", values, "id = ?", new String[]{String.valueOf(serviceId)});
        db.close();
        return result > 0;
    }

    public boolean deleteService(int serviceId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("services", "id = ?", new String[]{String.valueOf(serviceId)});
        db.close();
        return result > 0;
    }
}
