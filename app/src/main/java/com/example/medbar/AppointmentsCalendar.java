package com.example.medbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.medbar.Adapters.AppointmentAdapter;
import com.example.medbar.Objects.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AppointmentsCalendar extends AppCompatActivity {

    Button pickDate;
    int day_, month_;
    String date;

    DatabaseReference ref;
    ArrayList<Appointment> list;
    RecyclerView recyclerView;
    FirebaseAuth fAuth;
    AppointmentAdapter adapterClass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_appointments_calendar);

         toolbar();
        fAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);

        pickDate = (Button) findViewById(R.id.pickDate);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int  dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentsCalendar.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                pickDate.setText(day + " / " + (month+1) + " / " + year);
                                date = day + " / " + (month+1) + " / " + year;
                                day_ = day;
                                month_ = month+1;
                                findAppointments();
                            }

                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();



            }
        });

        list = new ArrayList<Appointment>();
        adapterClass = new AppointmentAdapter(list, this);
        recyclerView.setAdapter(adapterClass);


    }

    public void findAppointments() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String[] slot = {"","9:00", "10:00", "11:30", "1:00", "2:00","4:00", "4:30","6:30"};
        String month_s = String.valueOf(month_);
        String day_s = String.valueOf(day_);
        for (int slots = 1; slots <= 8; slots++) {
            ref = FirebaseDatabase.getInstance().getReference().child("Calendar").child(uid).child("2021").child(month_s).child(day_s).child("Timeslots").child(slot[slots]).child("Appointments");
            if (ref != null) {

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {


                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Appointment userObj = ds.getValue(Appointment.class);
                                list.add(userObj);
                                adapterClass.notifyItemInserted(list.size() - 1);
                                recyclerView.setAdapter(adapterClass);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AppointmentsCalendar.this, error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            }



        }





    }

    public void toolbar(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }


}