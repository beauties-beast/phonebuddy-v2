package com.github.beauties_beast.phonebuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by boggs on 10/2/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHelper";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PhoneBuddy.db";

    public static final String BUDDY_TABLE_NAME = "buddies";
    public static final String BUDDY_COLUMN_ID = "id";
    public static final String BUDDY_COLUMN_NUMBER = "number";

    public static final String NOTIFICATION_TABLE_NAME = "notificationconfig";
    public static final String NOTIFICATION_COLUMN_ID = "id";
    public static final String NOTIFICATION_COLUMN_PACKAGE_NAME = "packagename";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, String.format("Database create version %s.", DATABASE_VERSION));
        StringBuilder sql = new StringBuilder();

        sql.append(String.format("CREATE TABLE %s ", BUDDY_TABLE_NAME));
        sql.append("(");
        sql.append(String.format("%s %s, ", BUDDY_COLUMN_ID, "INTEGER PRIMARY KEY"));
        sql.append(String.format("%s %s ", BUDDY_COLUMN_NUMBER, "TEXT"));
        sql.append(")");
        db.execSQL(sql.toString());

        sql.append(String.format("CREATE TABLE %s ", NOTIFICATION_TABLE_NAME));
        sql.append("(");
        sql.append(String.format("%s %s, ", NOTIFICATION_COLUMN_ID, "INTEGER PRIMARY KEY"));
        sql.append(String.format("%s %s ", NOTIFICATION_COLUMN_PACKAGE_NAME, "TEXT"));
        sql.append(")");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("Database upgrade from %s to %s.", oldVersion, newVersion));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", BUDDY_TABLE_NAME));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", NOTIFICATION_TABLE_NAME));
        onCreate(db);
    }

    public void addBuddyPhone(BuddyPhone buddyPhone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BUDDY_TABLE_NAME, buddyPhone.getPhoneNumber());

        db.insert(BUDDY_TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<BuddyPhone> getBuddyPhones() {
        Log.d(TAG, String.format("Database getBuddyPhones()"));

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<BuddyPhone> buddyPhones = new ArrayList<BuddyPhone>();

        db.rawQuery(String.format("SELECT * FROM %s ", BUDDY_TABLE_NAME), null);

        Cursor cursor = db.query(BUDDY_TABLE_NAME,
            new String[]{ BUDDY_COLUMN_NUMBER },
            null, // id = ?
            null,
            null,
            null,
            null
        );

        if(cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndex(BUDDY_COLUMN_NUMBER));
                buddyPhones.add(new BuddyPhone("", number));
            } while(cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return buddyPhones;
    }
}