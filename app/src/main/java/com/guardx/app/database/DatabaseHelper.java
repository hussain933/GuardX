package com.guardx.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.guardx.app.utils.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {

    // History Table
    public static final String TABLE_HISTORY = "scan_history";
    public static final String COL_ID = "id";
    public static final String COL_DATE = "date";
    public static final String COL_TIME = "time";
    public static final String COL_THREATS = "threats";
    public static final String COL_ACTION = "action";
    public static final String COL_RISK = "risk_level";

    // Blacklist Table
    public static final String TABLE_BLACKLIST = "blacklist";
    public static final String COL_APP_NAME = "app_name";
    public static final String COL_PACKAGE = "package_name";
    public static final String COL_RISK_LEVEL = "risk_level";
    public static final String COL_ENABLED = "enabled";

    // Create Tables
    private static final String CREATE_HISTORY =
        "CREATE TABLE " + TABLE_HISTORY + " (" +
        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COL_DATE + " TEXT, " +
        COL_TIME + " TEXT, " +
        COL_THREATS + " TEXT, " +
        COL_ACTION + " TEXT, " +
        COL_RISK + " TEXT)";

    private static final String CREATE_BLACKLIST =
        "CREATE TABLE " + TABLE_BLACKLIST + " (" +
        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COL_APP_NAME + " TEXT, " +
        COL_PACKAGE + " TEXT UNIQUE, " +
        COL_RISK_LEVEL + " TEXT, " +
        COL_ENABLED + " INTEGER DEFAULT 1)";

    public DatabaseHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HISTORY);
        db.execSQL(CREATE_BLACKLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLACKLIST);
        onCreate(db);
    }
}
