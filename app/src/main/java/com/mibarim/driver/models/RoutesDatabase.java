package com.mibarim.driver.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.provider.BaseColumns;

import com.mibarim.driver.models.Plus.StationRouteModel;

import java.util.ArrayList;
import java.util.List;

import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.DESTINATION_STREET_ADDRESS;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.DESTINATION_STREET_ID;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.DESTINATION_STREET_LATITUDE;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.DESTINATION_STREET_LONGTITUDE;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.SOURCE_STREET_ADDRESS;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.SOURCE_STREET_ID;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.SOURCE_STREET_LATITUDE;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.SOURCE_STREET_LONGTITUDE;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.STREET_ROUTE_DURATION;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.STREET_ROUTE_ID;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.STREET_ROUTE_PRICE;
import static com.mibarim.driver.models.RoutesDatabase.MyRoutesContract.TABLE_NAME;

/**
 * Created by Alireza on 9/6/2017.
 */

public class RoutesDatabase {

    public static final class MyRoutesContract implements BaseColumns {



        // Table name
        public static final String TABLE_NAME = "RoutesTable";

        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String SOURCE_STREET_ADDRESS = "SrcStAdd";
        public static final String DESTINATION_STREET_ADDRESS = "DstStAdd";
        public static final String STREET_ROUTE_PRICE = "StRoutePrice";

        public static final String SOURCE_STREET_LATITUDE =  "SrcStLat";
        public static final String SOURCE_STREET_LONGTITUDE = "SrcStLng";


        public static final String STREET_ROUTE_ID = "StRouteId";
        public static final String SOURCE_STREET_ID = "SrcStId";
        public static final String  DESTINATION_STREET_ID = "DstStId";



        public static final String DESTINATION_STREET_LATITUDE = "DstStLat";
        public static final String DESTINATION_STREET_LONGTITUDE = "DstStLng";

        public static final String STREET_ROUTE_DURATION = "StRouteDuration";

    }



    public class RoutesDbHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "RouteReader.db";

        final String SQL_CREATE_ROUTES_TABLE = "CREATE TABLE " + MyRoutesContract.TABLE_NAME + " ("
                + SOURCE_STREET_ADDRESS + " TEXT,"
                + DESTINATION_STREET_ADDRESS + " TEXT,"

                + SOURCE_STREET_LATITUDE + " TEXT,"
                + STREET_ROUTE_ID + " TEXT,"
                + SOURCE_STREET_ID + " TEXT,"
                + DESTINATION_STREET_ID + " TEXT,"
                + SOURCE_STREET_LONGTITUDE + " TEXT,"
                + DESTINATION_STREET_LATITUDE + " TEXT,"
                + DESTINATION_STREET_LONGTITUDE + " TEXT,"
                + STREET_ROUTE_DURATION + " TEXT,"

                + STREET_ROUTE_PRICE + " TEXT" + ");";




        public RoutesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(SQL_CREATE_ROUTES_TABLE);
            //db.execSQL();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MyRoutesContract.TABLE_NAME);
            onCreate(db);
        }


    }


    Context context;
    RoutesDbHelper mDbHelper;
    SQLiteDatabase db;

    //constructor
    public RoutesDatabase(Context context) {

        this.context = context;

        mDbHelper = new RoutesDbHelper(context);
    }


    public void insertList(List<StationRouteModel> routeResponseList) {


        //RoutesDbHelper mDbHelper = new RoutesDbHelper();
        db = mDbHelper.getWritableDatabase();

        db.delete(MyRoutesContract.TABLE_NAME, null, null);


        ContentValues values;

        for (StationRouteModel routeResponse : routeResponseList) {

            SystemClock.sleep(100);
            values = new ContentValues();

            values.put(SOURCE_STREET_ADDRESS, routeResponse.SrcStAdd);
            values.put(DESTINATION_STREET_ADDRESS, routeResponse.DstStAdd);
            values.put(STREET_ROUTE_PRICE, routeResponse.StRoutePrice);
//
            values.put(STREET_ROUTE_ID, routeResponse.StRouteId);
            values.put(SOURCE_STREET_ID, routeResponse.SrcStId);
            values.put(DESTINATION_STREET_ID, routeResponse.DstStId);
            values.put(SOURCE_STREET_LATITUDE, routeResponse.SrcStLat);
            values.put(SOURCE_STREET_LONGTITUDE, routeResponse.SrcStLng);
            values.put(DESTINATION_STREET_LATITUDE, routeResponse.DstStLat);
            values.put(DESTINATION_STREET_LONGTITUDE, routeResponse.DstStLng);
            values.put(STREET_ROUTE_DURATION, routeResponse.StRouteDuration);




//            db.insert(MyRoutesContract.TABLE_NAME, null, values);
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);



        }
        //db.execSQL("select * from " + MyRoutesContract.TABLE_NAME);
        db.close();

    }


    public List<StationRouteModel> routeResponseListQuery() {
        db = mDbHelper.getReadableDatabase();
        StationRouteModel routeResponse = new StationRouteModel();
        List<StationRouteModel> routeResponseList = new ArrayList<StationRouteModel>();
        Cursor cursor = db.query(MyRoutesContract.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            routeResponse = new StationRouteModel();

            routeResponse.SrcStAdd = cursor.getString(cursor.getColumnIndex(MyRoutesContract.SOURCE_STREET_ADDRESS));
            routeResponse.DstStAdd = cursor.getString(cursor.getColumnIndex(MyRoutesContract.DESTINATION_STREET_ADDRESS));
            routeResponse.StRoutePrice = cursor.getString(cursor.getColumnIndex(MyRoutesContract.STREET_ROUTE_PRICE));


            routeResponse.SrcStLat = cursor.getString(cursor.getColumnIndex(MyRoutesContract.SOURCE_STREET_LATITUDE));
            routeResponse.SrcStLng = cursor.getString(cursor.getColumnIndex(MyRoutesContract.SOURCE_STREET_LONGTITUDE));
            routeResponse.DstStLat = cursor.getString(cursor.getColumnIndex(MyRoutesContract.DESTINATION_STREET_LATITUDE));
            routeResponse.DstStLng = cursor.getString(cursor.getColumnIndex(MyRoutesContract.DESTINATION_STREET_LONGTITUDE));
            routeResponse.StRouteDuration = cursor.getString(cursor.getColumnIndex(MyRoutesContract.STREET_ROUTE_DURATION));

            routeResponse.StRouteId = cursor.getLong(cursor.getColumnIndex(MyRoutesContract.STREET_ROUTE_ID));
            routeResponse.SrcStId = cursor.getLong(cursor.getColumnIndex(MyRoutesContract.SOURCE_STREET_ID));
            routeResponse.DstStId = cursor.getLong(cursor.getColumnIndex(MyRoutesContract.DESTINATION_STREET_ID));





            routeResponseList.add(routeResponse);
        }
        db.close();
        return routeResponseList;
    }


}
