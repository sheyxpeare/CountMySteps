package com.google.android.gms.fit.samples.DBLayout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;


/**
 * Main proxy for setting up local database on the device
 */
public class ProxyDB extends SQLiteOpenHelper {

    /*
    initializing table names and column names and the name of the local database
     */

    public static final String DATABASE_NAME = "pedometer.db";
    public static final String TABLE_NAME_ONE = "profile_table";
    public static final String Col_2_ONE = "Username";
    public static final String Col_3_ONE = "Age";
    public static final String Col_4_ONE = "Weight";
    public static final String Col_5_ONE = "Date";
    public static final String Col_6_ONE = "Step_Goal";
    public static final String Col_7_ONE = "act_goal";
    public HashMap<String, String> map;
    private Context mCtx;

    public ProxyDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
        mCtx = context;
    }
    /*
    Creating sqlite databse
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON");
        db.execSQL("create table " + TABLE_NAME_ONE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Username TEXT UNIQUE, Age INTEGER, Weight Integer, Date TEXT, Step_Goal INTEGER, act_goal INTEGER, FOREIGN KEY(Date) REFERENCES daily_counts_table(Date))");

    }
    /*
    Upgrading sqlite database
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_ONE);
        onCreate(db);

    }
    /*
    Inserting data into the usertable on the local device
     */

    public boolean insertDataT1(String username, int age, int weight, String date, int stepgoal, int calgoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Col_2_ONE, username);
        cv.put(Col_3_ONE, age);
        cv.put(Col_4_ONE, weight);
        cv.put(Col_5_ONE, date);
        cv.put(Col_6_ONE, stepgoal);
        cv.put(Col_7_ONE, calgoal);
        long result = db.insert(TABLE_NAME_ONE,null ,cv);
        if(result == -1)
            return false;
        else
            return true;
    }


    public boolean deleteData() {
        return false;
    }

    public boolean updateData() {
        return false;
    }
    public String readData() {
        return null;
    }

    /*
    Updating data in the table when user tries to change their goals or weight
     */

    public boolean updateDataT1(String username, int age, int weight, String date, int stepgoal, int calgoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Col_2_ONE, username);
        cv.put(Col_3_ONE, age);
        cv.put(Col_4_ONE, weight);
        cv.put(Col_5_ONE, date);
        cv.put(Col_6_ONE, stepgoal);
        cv.put(Col_7_ONE, calgoal);
        long result = db.update(TABLE_NAME_ONE,cv, "username = ?",new String[] { username });
        if(result == -1)
            return false;
        else
            return true;
    }

    //Fetch data from the database columns using a cursor
    public HashMap<String, String> fetchdata(){

        SQLiteDatabase db = this.getWritableDatabase();

        String[] allcols = new String[]{Col_3_ONE, Col_4_ONE, Col_6_ONE, Col_7_ONE};

        Cursor cursor = db.query(TABLE_NAME_ONE, allcols, null, null, null, null, null);
        map = new HashMap<String, String>();


        for (cursor.moveToFirst(); !cursor.isAfterLast() ; cursor.moveToNext()){
            map.put(Col_3_ONE, cursor.getString(0).toString());
            map.put(Col_4_ONE, cursor.getString(1).toString());
            map.put(Col_6_ONE, cursor.getString(2).toString());
            map.put(Col_7_ONE, cursor.getString(3).toString());
        }
        Log.i("MAP:", map.toString());
        return map;

    }
}
