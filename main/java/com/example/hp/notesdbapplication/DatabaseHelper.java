package com.example.hp.notesdbapplication;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notesDB.db";
    private static final int SCHEMA = 1;
    static final String TABLE = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESC = "descText";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_CLASS = "noteClass";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Notes (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME
                + " TEXT, " + COLUMN_DESC + " TEXT, " + COLUMN_CLASS + " INTEGER, " + COLUMN_DATE + " DATE,"+COLUMN_IMAGE + " TEXT);");
        // добавление начальных данных

        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_NAME
                + ", " + COLUMN_DESC +", " + COLUMN_CLASS + " , " +COLUMN_DATE+", "+COLUMN_IMAGE +") VALUES ('Том jjСит','nk', 78,date('now'),'jj');");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }
}