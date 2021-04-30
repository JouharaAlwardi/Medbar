package com.example.medbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medbar.Adapters.LabtestsAdapter;
import com.example.medbar.Adapters.ResultAdapter;
import com.example.medbar.Objects.Labtest;
import com.example.medbar.Objects.Result;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewResults extends AppCompatActivity {

    String name;
    TextView patientName_ID;

    DatabaseReference ref;
    ArrayList<Result> list;
    RecyclerView recyclerView;
    FirebaseAuth fAuth;
    ResultAdapter adapterClass;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_view_results);
        toolbar();

        patientName_ID = (TextView) findViewById(R.id.patientName_ID_rs);
        name = getIntent().getExtras().getString("patientName");
        patientName_ID.setText("Results For Patient "+name);

        searchView = findViewById(R.id.searchView_rs);
        fAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.rv_rs);

        list = new ArrayList<Result>();
        adapterClass = new ResultAdapter(list, this);
        recyclerView.setAdapter(adapterClass);

        //Print Result Collection in RV with two buttons
        //View Images + comments
        //View Report + comments

        onstart();

    }

    public void onstart() {

        String[] slot = {"","Cellular And Chemical Analysis", "Diagnostic Imaging", "Measurement", "Vital Signs"};
        for(int month = 6; month <= 6 ; month++) {
            String month_s = String.valueOf(month);
            for(int day = 1; day <= 30 ; day++) {
                String day_s = String.valueOf(day);
                for (int slots = 1; slots <= 4; slots++) {
                    for (int test = 1; test <= 4; test++) {
                        ref =  FirebaseDatabase.getInstance().getReference().child("CalendarLabtests").child("2021").child(month_s).child(day_s).child("MedicalTests").child(slot[slots]).child("Test"+ String.valueOf(test)).child("Results");

                        if (ref != null) {
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {

                                        for (DataSnapshot ds : snapshot.getChildren()) {

                                            String key = ds.getKey();
                                            if (key.equalsIgnoreCase("Results_"+ name)) {

                                                String medtest = ds.child("medical_Test_Type").getValue(String.class);
                                                String patientID = ds.child("patient_Name_ID").getValue(String.class);
                                                String testname = ds.child("test_Name").getValue(String.class);
                                                String testdate = ds.child("test_Date").getValue(String.class);
                                                Result newResult = new Result(medtest, patientID, testname,testdate);

                                                list.add(newResult);
                                                adapterClass.notifyItemInserted(list.size() - 1);
                                                recyclerView.setAdapter(adapterClass);
                                            }
                                        }


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ViewResults.this, error.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
                        }


                    } }}}



    }

    public void toolbar(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FindPatientLabtests.class));
                finish();
            }
        });
    }
}