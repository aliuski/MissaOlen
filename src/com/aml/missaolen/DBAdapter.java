package com.aml.missaolen;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
    static final String KEY_DATE = "date";
    static final String KEY_LAT = "lat";
    static final String KEY_LNG = "lon";
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "locations";
    static final String DATABASE_TABLE = "locations";
    static final int DATABASE_VERSION = 1;

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    Calendar currentDate;
    SimpleDateFormat formatter; 
    
    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
        currentDate = Calendar.getInstance();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) { }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    }

    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() 
    {
        DBHelper.close();
    }

    public long insertLocation(String lat, String lng) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DATE, formatter.format(currentDate.getTime()));
        initialValues.put(KEY_LAT, lat);
        initialValues.put(KEY_LNG, lng);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public DBAdapter readableopen() throws SQLException 
    {
    	db = DBHelper.getReadableDatabase();
        return this;
    }
    
    public Cursor getLocations(String whereClause,String[] whereArgs,String orderBy)
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_DATE, KEY_LAT,
                KEY_LNG}, whereClause, whereArgs, null, null, orderBy);
    }
    
    public boolean deleteLocations(String date)
    {
    	String[] st = new String[]{
    			(new java.sql.Date(java.sql.Date.valueOf(date).getTime()-86400000)).toString(),
    			(new java.sql.Date( java.sql.Date.valueOf(date).getTime()+86400000)).toString()};
    	return db.delete(DATABASE_TABLE,KEY_DATE+">? and "+KEY_DATE+"<?",st) > 0;
    }
}
