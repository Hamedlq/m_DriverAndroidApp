package com.mibarim.driver.dataBase;

import android.provider.BaseColumns;

/**
 * Created by Arya on 1/24/2018.
 */

public class CurrentLocationContract implements BaseColumns {

    final static String LATITUDE = "latitude";
    final static String LONGITUDE = "longitude";
    final static String TRIPID = "tripId";
    final static String TRIPSTATE = "tripState";
    final static String TABLE_NAME = "locations";

    final static String ALL_DATA = "SELECT * FROM " + TABLE_NAME;

    final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY," + LATITUDE + " TEXT NOT NULL,"
            + LONGITUDE + " TEXT NOT NULL," + TRIPID + " INTEGER NOT NULL,"
            + TRIPSTATE + " INTEGER NOT NULL," + "UNIQUE ( " + _ID + ") ON CONFLICT REPLACE" + ")";
}
