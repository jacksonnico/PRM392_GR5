package com.example.prm392_gr5.Data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.UserMessageSummary;

import java.util.ArrayList;
import java.util.List;

public class OwnerChatSummaryRepository {
    private final DatabaseHelper dbHelper;
    private final UserProfileRepository userProfileRepo;

    public OwnerChatSummaryRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
        userProfileRepo = new UserProfileRepository(context);
    }

    public List<UserMessageSummary> getUserMessageSummariesForOwner(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<UserMessageSummary> summaries = new ArrayList<>();

        Cursor pitchCursor = null;

        try {
            // Lấy danh sách sân của chủ sân
            pitchCursor = db.rawQuery(
                    "SELECT name FROM " + DatabaseHelper.TBL_PITCHES + " WHERE ownerId = ?",
                    new String[]{String.valueOf(ownerId)}
            );

            if (pitchCursor != null && pitchCursor.moveToFirst()) {
                do {
                    String pitchName = pitchCursor.getString(pitchCursor.getColumnIndexOrThrow("name"));

                    // Lấy tin nhắn mới nhất của mỗi user trong sân
                    Cursor messageCursor = db.rawQuery(
                            "SELECT sender, message, time, userId " +
                                    "FROM " + DatabaseHelper.TBL_MESSAGES +
                                    " WHERE pitchName = ? AND userId > 0 " +
                                    "ORDER BY id DESC LIMIT 1",
                            new String[]{pitchName}
                    );

                    if (messageCursor != null && messageCursor.moveToFirst()) {
                        int userId = messageCursor.getInt(messageCursor.getColumnIndexOrThrow("userId"));
                        String lastMessage = messageCursor.getString(messageCursor.getColumnIndexOrThrow("message"));
                        String lastTime = messageCursor.getString(messageCursor.getColumnIndexOrThrow("time"));

                        // Lấy thông tin người dùng
                        String displayName = userProfileRepo.getFullNameFromUserId(userId);
                        String phone = userProfileRepo.getPhoneNumberFromUserId(userId);

                        summaries.add(new UserMessageSummary(
                                userId,
                                displayName != null ? displayName : "Khách hàng",
                                lastMessage,
                                lastTime,
                                pitchName,
                                phone != null ? phone : "Không rõ"
                        ));
                    } else {
                        Log.d("OwnerChatSummaryRepo", "Không tìm thấy tin nhắn nào cho sân: " + pitchName);
                    }

                    if (messageCursor != null) {
                        messageCursor.close();
                    }

                } while (pitchCursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("OwnerChatSummaryRepo", "Lỗi khi lấy danh sách tin nhắn: " + e.getMessage());
        } finally {
            if (pitchCursor != null) pitchCursor.close();
            db.close();
        }

        return summaries;
    }
}
