package com.example.geocoding;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.Nullable;

public class GeoDatabase extends SQLiteOpenHelper {
    public static GeoDatabase sInstance;
    public static final int DB_VERSION = 2;
    public static String DB_NAME = "geo_db";
    public static String DB_TABLE = "geo_table";
    public static String COLUMN_ID = "geo_id";
    public static String COLUMN_LATITUDE = "geo_lat";
    public static String COLUMN_LONGITUDE = "geo_long";
    public static String COLUMN_ADDRESS = "geo_add";


    private GeoDatabase(@Nullable Context context) {super (context, DB_NAME, null, DB_VERSION); }

    public static synchronized GeoDatabase getInstance(Context context){
        if (sInstance == null){
            sInstance = new GeoDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + DB_TABLE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LATITUDE + " FLOAT," +
                COLUMN_LONGITUDE + " FLOAT," +
                COLUMN_ADDRESS + " TEXT" + ")";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= newVersion) {
            return;
        }

        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    public void addLoc(Location location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LATITUDE, location.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, location.getLongitude());
        contentValues.put(COLUMN_ADDRESS, location.getAddress());
        long id = db.insert(DB_TABLE, null, contentValues);
        Log.d("Inserted", "id --> " + id);
    }

    public void updateLoc(int id, double latitude, double longitude, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LATITUDE, latitude);
        contentValues.put(COLUMN_LONGITUDE, longitude);
        contentValues.put(COLUMN_ADDRESS, address);
        db.update(DB_TABLE, contentValues, "geo_id=?", new String[] {String.valueOf(id)});
        Log.d("Updated", "id --> " + id);
    }

    public Location getLocByAddress(String address) {
        SQLiteDatabase db = this.getReadableDatabase();
//        String[] query = new String[] {COLUMN_ID, COLUMN_LATITUDE, COLUMN_LATITUDE, COLUMN_ADDRESS};
//        Cursor cursor = db.query(DB_TABLE, query, COLUMN_ID+"=?", new String[]{String.valueOf(address)}, null, null, null);
        Cursor cursor=db.rawQuery("select distinct * from "+DB_TABLE+" where "+COLUMN_ADDRESS+" LIKE \"%"+address+"%\"", new String[]{});
        Location loc;
        if (cursor.getCount()==0) {return null;}
        else {
            cursor.moveToFirst();
            loc = new Location(
                    cursor.getDouble(1),
                    cursor.getDouble(2),
                    cursor.getString(3));
            cursor.close();
            return loc;
        }
    }

    void deleteLocByAddress(String address) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select distinct * from "+DB_TABLE+" where "+COLUMN_ADDRESS+" LIKE \"%"+address+"%\"", new String[]{});
        Location loc = new Location();
        if (cursor.getCount()!=0) {cursor.moveToFirst();
            loc = new Location(
                    cursor.getInt(0),
                    cursor.getDouble(1),
                    cursor.getDouble(2),
                    cursor.getString(3));
            cursor.close();
        }
        db.delete(DB_TABLE, COLUMN_ID+"=?", new String[]{String.valueOf(loc.getId())});
        db.close();
    }

    void resetTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE " + DB_TABLE);

        String CREATE_TABLE = "CREATE TABLE " + DB_TABLE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LONGITUDE + " FLOAT," +
                COLUMN_LATITUDE + " FLOAT," +
                COLUMN_ADDRESS + " TEXT" + ")";

        db.execSQL(CREATE_TABLE);
    }
}
