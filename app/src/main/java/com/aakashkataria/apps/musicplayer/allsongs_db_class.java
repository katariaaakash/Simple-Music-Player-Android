package com.aakashkataria.apps.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by aakashkataria on 25/01/18.
 */

public class allsongs_db_class extends SQLiteOpenHelper {
    public static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SONGS_Db";
    public static final String TABLE_NAME_ALL = "ALL_SONGS";
    public static final String COL1_ALL = "location";
    public static final String COL2_ALL = "name";
    public allsongs_db_class(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table ALL_SONGS (location TEXT PRIMARY KEY, name TEXT);");
    }

    public Cursor read_data(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME_ALL + ";", null);
        return cursor;
    }

    public void write_data(String loc, String name){
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("insert into " + TABLE_NAME_ALL + " values ('" + loc + "', '" + name + "');");
        }
        catch (Exception e){
            // DO NOTHING
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP table if exists ALL_SONGS");
    }
}
