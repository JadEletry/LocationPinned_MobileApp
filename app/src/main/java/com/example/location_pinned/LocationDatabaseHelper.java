package com.example.location_pinned;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LocationDatabase.db";
    private static final int DATABASE_VERSION = 2;

    // SQL statement to create the location table
    private static final String CREATE_TABLE_LOCATION =
            "CREATE TABLE location (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "address TEXT," +
                    "latitude REAL," +
                    "longitude REAL)";

    public LocationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the location table
        db.execSQL(CREATE_TABLE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE location ADD COLUMN new_column_name TEXT;");
        }
    }

}
