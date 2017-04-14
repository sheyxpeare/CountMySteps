package com.google.android.gms.fit.samples.DBLayout;

import android.database.sqlite.SQLiteDatabase;

/**
 * interface for creating database
 */
public interface CreateDBInterface {

    public void onCreate(SQLiteDatabase db);
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
