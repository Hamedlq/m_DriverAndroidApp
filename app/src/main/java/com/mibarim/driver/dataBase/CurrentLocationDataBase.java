package com.mibarim.driver.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mibarim.driver.models.Trip.LocationDataBaseModel;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.mibarim.driver.dataBase.CurrentLocationContract.ALL_DATA;
import static com.mibarim.driver.dataBase.CurrentLocationContract.CREATE_TABLE;
import static com.mibarim.driver.dataBase.CurrentLocationContract.LATITUDE;
import static com.mibarim.driver.dataBase.CurrentLocationContract.LONGITUDE;
import static com.mibarim.driver.dataBase.CurrentLocationContract.TABLE_NAME;
import static com.mibarim.driver.dataBase.CurrentLocationContract.TRIPID;
import static com.mibarim.driver.dataBase.CurrentLocationContract.TRIPSTATE;

/**
 * Created by Arya on 1/24/2018.
 */

public class CurrentLocationDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "current_location.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public CurrentLocationDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void openDB() {
        db = getWritableDatabase();
    }

    public void closeDB() {
        db.close();
    }

    public void inserting(String latitude, String longitude, long tripId, int tripState) {

        ContentValues values = new ContentValues();
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(TRIPID, tripId);
        values.put(TRIPSTATE, tripState);

        db.insert(TABLE_NAME, null, values);
    }

    public List<LocationDataBaseModel> getAllData() {
        List<LocationDataBaseModel> modelList = new ArrayList<>();
        Cursor cursor = db.rawQuery(ALL_DATA, null);
        try {
            while (cursor.moveToNext()) {
                LocationDataBaseModel model = new LocationDataBaseModel(
                        cursor.getString(cursor.getColumnIndex(LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(LONGITUDE)),
                        cursor.getLong(cursor.getColumnIndex(TRIPID)),
                        cursor.getInt(cursor.getColumnIndex(TRIPSTATE)));
                modelList.add(model);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return modelList;
    }

    public void deleteAll() {
        db.delete(TABLE_NAME, null, null);
    }
}
