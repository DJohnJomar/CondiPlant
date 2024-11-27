package com.example.condiplant;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String REPORT_TABLE = "REPORT_TABLE";
    public static final String COLUMN_PLANT_NAME = "PLANT_NAME";
    public static final String COLUMN_PLANT_DISEASE_NAME = "PLANT_DISEASE_NAME";
    public static final String COLUMN_CAPTURE_DATE = "CAPTURE_DATE";
    public static final String COLUMN_ID = "ID";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "report.db", null, 1);
    }

    //Called the first time the database is accessed.
    //There should be a code to create a new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + REPORT_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PLANT_NAME + " TEXT, " + COLUMN_PLANT_DISEASE_NAME + " TEXT, " + COLUMN_CAPTURE_DATE + "  TEXT)";
        db.execSQL(createTableStatement);

    }

    //Called whenever the code in your database changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean addOne(ReportsModel reportsModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PLANT_NAME, reportsModel.getPlantName());
        cv.put(COLUMN_PLANT_DISEASE_NAME, reportsModel.getDiseaseName());
        cv.put(COLUMN_CAPTURE_DATE, reportsModel.getCaptureDate());

        long insert = db.insert(REPORT_TABLE, null, cv);
        if(insert == -1){
            return false;
        }else{
            return true;
        }
    }
}
