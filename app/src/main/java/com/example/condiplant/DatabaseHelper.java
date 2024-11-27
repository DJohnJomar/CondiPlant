package com.example.condiplant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    //Uses ReportItem - report class specialized for db storing
    public boolean addOne(ReportItem reportItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PLANT_NAME, reportItem.getPlantName());
        cv.put(COLUMN_PLANT_DISEASE_NAME, reportItem.getDiseaseName());
        cv.put(COLUMN_CAPTURE_DATE, reportItem.getCaptureDate());

        long insert = db.insert(REPORT_TABLE, null, cv);
        if(insert == -1){
            return false;
        }else{
            return true;
        }
    }



    // Method to fetch records based on selected "yyyy-MM" formatted date string
    // Uses ReportsModel - report object specialized for RecyclerView and displaying
    public ArrayList<ReportsModel> getSelectedDate(String yearMonth) {
        ArrayList<ReportsModel> returnList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query to get the count of each (PLANT_NAME, PLANT_DISEASE_NAME) pair
        String queryString = "SELECT " + COLUMN_PLANT_NAME + ", " + COLUMN_PLANT_DISEASE_NAME + ", COUNT(*) as count FROM " + REPORT_TABLE +
                " WHERE " + COLUMN_CAPTURE_DATE + " LIKE ? GROUP BY " + COLUMN_PLANT_NAME + ", " + COLUMN_PLANT_DISEASE_NAME;
        Log.d("DatabaseQuery", "Query: " + queryString);  // Log the query
        // Query with the "yyyy-MM%" format to match all entries for that month and year
        Cursor cursor = db.rawQuery(queryString, new String[]{yearMonth + "%"}); // yearMonth is "yyyy-MM"

        if (cursor.moveToFirst()) {
            do {
                // Access the plant name, disease name, and count from the cursor
                String plantName = cursor.getString(0); // Assuming COLUMN_PLANT_NAME is at index 0
                String diseaseName = cursor.getString(1); // Assuming COLUMN_PLANT_DISEASE_NAME is at index 1
                int count = cursor.getInt(2); // The count from COUNT(*) alias 'count' will be at index 2
                // Log the results
                Log.d("DatabaseQuery", "Plant: " + plantName + ", Disease: " + diseaseName + ", Count: " + count);
                // Add the formatted result to the list
                returnList.add(new ReportsModel(plantName, diseaseName, count));
            } while (cursor.moveToNext());
        }else {
            Log.d("DatabaseQuery", "No data found for " + yearMonth);
        }

        cursor.close();
        db.close();
        return returnList;
    }

}
