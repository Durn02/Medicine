package com.example.medicine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBhelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Status.db";

    public DBhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_create = "CREATE TABLE if not exists "
                + TableInfo.TABLE_NAME
                +" ("
                + TableInfo.COLUMN_NAME_ID + " integer primary key autoincrement, "
                + TableInfo.COLUMN_NAME_TYPE + " text, "
                + TableInfo.COLUMN_NAME_DATE + " text, "
                + TableInfo.COLUMN_NAME_NUM +  " integer"
                + ");";

        db.execSQL(sql_create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql_delete = "DROP TABLE if exists "+ TableInfo.TABLE_NAME;

        db.execSQL(sql_delete);
        onCreate(db);
    }

}