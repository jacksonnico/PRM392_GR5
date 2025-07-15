package com.example.prm392_gr5.Data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

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

    public boolean addMessage(Message message, int ownerId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String senderName = message.getSender();
            if (senderName == null || senderName.isEmpty()) {
                senderName = message.getUserId() > 0 ? "Khách hàng" : "Chủ sân";
            }

            ContentValues values = new ContentValues();
            values.put("sender", senderName);
            values.put("message", message.getMessage());
            values.put("time", message.getTime());
            values.put("pitchName", message.getPitchName());
            values.put("userId", message.getUserId());
            values.put("ownerId", ownerId);

            long result = db.insert(DatabaseHelper.TBL_MESSAGES, null, values);
            if (result == -1) {
                Toast.makeText(context, "Không thể gửi tin nhắn, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                Log.e("ChatMessageRepository", "Failed to insert message for pitch: " + message.getPitchName());
                return false;
            }

            Log.d("ChatMessageRepository", "Message inserted: " + senderName + " - " + message.getMessage());
            return true;
        } catch (Exception e) {
            Log.e("ChatMessageRepository", "Error adding message: " + e.getMessage());
            Toast.makeText(context, "Lỗi hệ thống, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            db.close();
        }
    }

    public List<Message> getMessagesByPitch(String pitchName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Message> messages = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TBL_MESSAGES, null, "pitchName = ?",
                new String[]{pitchName}, null, null, "id ASC");
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
                String msg = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
                int ownerId = cursor.getInt(cursor.getColumnIndexOrThrow("ownerId"));
                messages.add(new Message(id, sender, msg, time, pitchName, userId, ownerId));
            }
            Log.d("ChatMessageRepository", "Fetched " + messages.size() + " messages for pitch: " + pitchName);
        } catch (Exception e) {
            Log.e("ChatMessageRepository", "Error fetching messages for pitch " + pitchName + ": " + e.getMessage());
            Toast.makeText(context, "Không thể tải tin nhắn", Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
            db.close();
        }
        return messages;
    }

    public List<String> getAllPitchNamesWithMessages() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT pitchName FROM " + DatabaseHelper.TBL_MESSAGES, null);
        List<String> pitchNames = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String pitchName = cursor.getString(cursor.getColumnIndexOrThrow("pitchName"));
                pitchNames.add(pitchName);
            }
        } catch (Exception e) {
            Log.e("ChatMessageRepository", "Error getting pitch names: " + e.getMessage());
        } finally {
            cursor.close();
            db.close();
        }
        return pitchNames;
    }

    public Message getLastMessageByPitch(String pitchName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TBL_MESSAGES, null, "pitchName = ?",
                new String[]{pitchName}, null, null, "id DESC", "1");
        Message lastMessage = null;
        try {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
                String msg = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
                int ownerId = cursor.getInt(cursor.getColumnIndexOrThrow("ownerId"));
                lastMessage = new Message(id, sender, msg, time, pitchName, userId, ownerId);
            }
        } catch (Exception e) {
            Log.e("ChatMessageRepository", "Error getting last message: " + e.getMessage());
        } finally {
            cursor.close();
            db.close();
        }
        return lastMessage;
    }
}