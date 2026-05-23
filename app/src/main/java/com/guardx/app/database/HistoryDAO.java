package com.guardx.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public HistoryDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void addHistory(String threats, String action, String riskLevel) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DATE, date);
        values.put(DatabaseHelper.COL_TIME, time);
        values.put(DatabaseHelper.COL_THREATS, threats);
        values.put(DatabaseHelper.COL_ACTION, action);
        values.put(DatabaseHelper.COL_RISK, riskLevel);
        db.insert(DatabaseHelper.TABLE_HISTORY, null, values);
    }

    public List<Map<String, String>> getAllHistory() {
        List<Map<String, String>> list = new ArrayList<>();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_HISTORY,
            null, null, null, null, null,
            DatabaseHelper.COL_ID + " DESC"
        );

        while (cursor.moveToNext()) {
            Map<String, String> row = new HashMap<>();
            row.put("date", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE)));
            row.put("time", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TIME)));
            row.put("threats", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_THREATS)));
            row.put("action", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ACTION)));
            row.put("risk", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RISK)));
            list.add(row);
        }
        cursor.close();
        return list;
    }

    public void clearHistory() {
        db.delete(DatabaseHelper.TABLE_HISTORY, null, null);
    }
}
