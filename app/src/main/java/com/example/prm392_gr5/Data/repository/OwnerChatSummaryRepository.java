package com.example.prm392_gr5.Data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.UserMessageSummary;

import java.util.ArrayList;
import java.util.List;

public class OwnerChatSummaryRepository {
    private final DatabaseHelper dbHelper;

    public OwnerChatSummaryRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<UserMessageSummary> getUserMessageSummariesForOwner(int ownerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor pitchCursor = null;
        Cursor messageCursor = null;
        List<UserMessageSummary> summaries = new ArrayList<>();
        try {
            pitchCursor = db.rawQuery("SELECT name FROM " + DatabaseHelper.TBL_PITCHES + " WHERE ownerId = ?", new String[]{String.valueOf(ownerId)});
            if (pitchCursor.moveToFirst()) {
                do {
                    String pitchName = pitchCursor.getString(pitchCursor.getColumnIndexOrThrow("name"));

                    messageCursor = db.rawQuery("SELECT sender, message, time, userId FROM " + DatabaseHelper.TBL_MESSAGES +
                            " WHERE pitchName = ? ORDER BY id DESC LIMIT 1", new String[]{pitchName});
                    if (messageCursor.moveToFirst()) {
                        int userId = messageCursor.getInt(messageCursor.getColumnIndexOrThrow("userId"));
                        String lastMessage = messageCursor.getString(messageCursor.getColumnIndexOrThrow("message"));
                        String lastTime = messageCursor.getString(messageCursor.getColumnIndexOrThrow("time"));

                        String displayName = new UserProfileRepository(dbHelper.getContext()).getFullNameFromUserId(userId);
                        String phone = new UserProfileRepository(dbHelper.getContext()).getPhoneNumberFromUserId(userId);

                        summaries.add(new UserMessageSummary(
                                userId,
                                displayName,
                                lastMessage,
                                lastTime,
                                pitchName,
                                phone
                        ));
                    }

                    if (messageCursor != null) messageCursor.close();
                } while (pitchCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pitchCursor != null) pitchCursor.close();
            if (messageCursor != null) messageCursor.close();
            db.close();
        }
        return summaries;
    }
}
