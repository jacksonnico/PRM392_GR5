package com.example.prm392_gr5.Data.repository;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageRepository {
    private final DatabaseHelper dbHelper;
    private final Context context;

    public ChatMessageRepository(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void addMessage(Message message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();

            String senderName = message.getSender();
            if (senderName == null || senderName.isEmpty()) {
                // ✅ Logic đúng: userId > 0 = user, userId = 0 = system/admin
                if (message.getUserId() > 0) {
                    senderName = "Khách hàng"; // User gửi tin nhắn
                } else {
                    senderName = "Hệ thống"; // System message
                }
            }

            values.put("sender", senderName);
            values.put("message", message.getMessage());
            values.put("time", message.getTime());
            values.put("pitchName", message.getPitchName());
            values.put("userId", message.getUserId());

            long result = db.insert(DatabaseHelper.TBL_MESSAGES, null, values);
            Log.d("ChatMessageRepository", "Message inserted: " + senderName + " - " + message.getMessage());

        } catch (Exception e) {
            Log.e("ChatMessageRepository", "Error adding message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public List<Message> getMessagesByPitch(String pitchName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        List<Message> messages = new ArrayList<>();

        try {
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TBL_MESSAGES +
                    " WHERE pitchName = ? ORDER BY id ASC", new String[]{pitchName});

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
                String msg = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));

                messages.add(new Message(id, sender, msg, time, pitchName, userId));
            }

            Log.d("ChatMessageRepository", "Retrieved " + messages.size() + " messages for pitch: " + pitchName);

        } catch (Exception e) {
            Log.e("ChatMessageRepository", "Error getting messages: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return messages;
    }

    // ✅ Thêm method để lấy tất cả pitch names có tin nhắn
    public List<String> getAllPitchNamesWithMessages() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        List<String> pitchNames = new ArrayList<>();

        try {
            cursor = db.rawQuery("SELECT DISTINCT pitchName FROM " + DatabaseHelper.TBL_MESSAGES, null);

            while (cursor.moveToNext()) {
                String pitchName = cursor.getString(cursor.getColumnIndexOrThrow("pitchName"));
                pitchNames.add(pitchName);
            }

        } catch (Exception e) {
            Log.e("ChatMessageRepository", "Error getting pitch names: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return pitchNames;
    }

    // ✅ Thêm method để lấy tin nhắn cuối cùng của một pitch
    public Message getLastMessageByPitch(String pitchName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Message lastMessage = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TBL_MESSAGES +
                    " WHERE pitchName = ? ORDER BY id DESC LIMIT 1", new String[]{pitchName});

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
                String msg = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));

                lastMessage = new Message(id, sender, msg, time, pitchName, userId);
            }

        } catch (Exception e) {
            Log.e("ChatMessageRepository", "Error getting last message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lastMessage;
    }
}