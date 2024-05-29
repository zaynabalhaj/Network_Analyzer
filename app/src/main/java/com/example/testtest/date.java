package com.example.testtest;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class date extends AppCompatActivity {
    EditText datefotmat;
    int Year;
    int month;
    int day;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.date_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        datefotmat=findViewById(R.id.dateformatID);
        Calendar calendar= Calendar.getInstance();
        datefotmat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                Year=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(date.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        datefotmat.setText(SimpleDateFormat.getDateInstance().format(calendar.getTime()));
                    }
                }, Year,month,day);
                datePickerDialog.show();



            }
        });

        datefotmat=findViewById(R.id.dateformatID1);
        Calendar calendar1= Calendar.getInstance();
        datefotmat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Year=calendar1.get(Calendar.YEAR);
                month=calendar1.get(Calendar.MONTH);
                Year=calendar1.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(date.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        datefotmat.setText(SimpleDateFormat.getDateInstance().format(calendar1.getTime()));
                    }
                }, Year,month,day);
                datePickerDialog.show();



            }
        });
    }
}
