package com.guardx.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlacklistDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public BlacklistDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void addApp(String name, String packageName, String riskLevel) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_APP_NAME, name);
        values.put(DatabaseHelper.COL_PACKAGE, packageName);
        values.put(DatabaseHelper.COL_RISK_LEVEL, riskLevel);
        values.put(DatabaseHelper.COL_ENABLED, 1);
        db.insertWithOnConflict(
            DatabaseHelper.TABLE_BLACKLIST,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    public void toggleApp(String packageName, boolean enabled) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ENABLED, enabled ? 1 : 0);
        db.update(
            DatabaseHelper.TABLE_BLACKLIST,
            values,
            DatabaseHelper.COL_PACKAGE + "=?",
            new String[]{packageName}
        );
    }

    public void removeApp(String packageName) {
        db.delete(
            DatabaseHelper.TABLE_BLACKLIST,
            DatabaseHelper.COL_PACKAGE + "=?",
            new String[]{packageName}
        );
    }

    public List<Map<String, String>> getAllApps() {
        List<Map<String, String>> list = new ArrayList<>();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_BLACKLIST,
            null, null, null, null, null,
            DatabaseHelper.COL_APP_NAME + " ASC"
        );

        while (cursor.moveToNext()) {
            Map<String, String> row = new HashMap<>();
            row.put("name", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_APP_NAME)));
            row.put("package", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PACKAGE)));
            row.put("risk", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RISK_LEVEL)));
            row.put("enabled", String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ENABLED))));
            list.add(row);
        }
        cursor.close();
        return list;
    }

    public List<String> getEnabledPackages() {
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_BLACKLIST,
            new String[]{DatabaseHelper.COL_PACKAGE},
            DatabaseHelper.COL_ENABLED + "=1",
            null, null, null, null
        );

        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        return list;
    }
}
