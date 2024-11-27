package com.example.condiplant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.condiplant.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class Report extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnDatePicker;
    private DatePickerDialog datePickerDialog;
    private ArrayList<ReportsModel> reportsModelList;
    private String btnTextSelectedDate;//Date String for the datePicker button
    private String selectedDate;
    private Reports_RecyclerViewAdapter adapter;
    RecyclerView reportRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// Finish current activity and go back to previous activity (MainActivity)
            }
        });

        selectedDate = getTodaysDateFormatted();// Initialize selectedDate with today's date
        initMonthYearPicker();

        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnDatePicker.setText(getTodaysDate());
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });

        //Create your adapter after you setup your model
        reportRecyclerView = findViewById(R.id.reportRecyclerView);
        updateRecyclerViewData(selectedDate);

        adapter = new Reports_RecyclerViewAdapter(this, reportsModelList);
        //Attaching the adapter to the recyclerview
        reportRecyclerView.setAdapter(adapter);
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private String getTodaysDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1; // Month is set to 0 (January is 0). This is to set it to 1 as january is in the calendar
        return makeDateString(month, year);
    }
    private String getTodaysDateFormatted(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1; // Month is set to 0 (January is 0). This is to set it to 1 as january is in the calendar
        return year+"-"+month;
    }

    private void initMonthYearPicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            //Use the values inside onDateSet for other purposes e.g. for database, etc.
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d("MonthYearPicker", "Selected Month: " + (month + 1) + ", Selected Year: " + year);
                // Ignore dayOfMonth and format output as Month-Year
                btnTextSelectedDate = getMonthFormat(month + 1) + " " + year;
                btnDatePicker.setText(btnTextSelectedDate);
                selectedDate = year+"-"+(month+1);
                updateRecyclerViewData(selectedDate);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, 1);
        // Suppress the calendar view (only spinner mode)
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
    }

    private String makeDateString(int month, int year){
        return getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat (int month){
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";
        //Default should never happen
        return "JAN";
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }

    // This method will query the database and update the RecyclerView's adapter
    private void updateRecyclerViewData(String selectedDate) {
        // Fetch the data based on the selected date
        DatabaseHelper databaseHelper = new DatabaseHelper(Report.this);
        reportsModelList = databaseHelper.getSelectedDate(selectedDate); // Assume this method fetches data based on date
        Log.d("RecyclerViewData", "Fetched data: " + reportsModelList.size() + " items");

        // Check if reportsModelList has data
        if (reportsModelList != null && !reportsModelList.isEmpty()) {
            adapter = new Reports_RecyclerViewAdapter(this, reportsModelList);
            reportRecyclerView.setAdapter(adapter);  // Set adapter only once after fetching data
            adapter.notifyDataSetChanged(); // Notify the adapter of the updated data
        } else {
            Log.d("RecyclerViewData", "No data found for selected date.");
        }
    }

}