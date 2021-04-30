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
import com.example.medbar.Objects.Labtest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewLabtests extends AppCompatActivity {

    String name;
    TextView patientName_ID;

    DatabaseReference ref;
    ArrayList<Labtest> list;
    RecyclerView recyclerView;
    FirebaseAuth fAuth;
    LabtestsAdapter adapterClass;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_labtests);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        toolbar();
        searchView = findViewById(R.id.searchView);

        patientName_ID = (TextView) findViewById(R.id.patientName_ID);
        name = getIntent().getExtras().getString("patientName");
        patientName_ID.setText("Labtests For Patient "+name);


        fAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.rv);

        list = new ArrayList<Labtest>();
        adapterClass = new LabtestsAdapter(list, this);
        recyclerView.setAdapter(adapterClass);

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
                                    ref =  FirebaseDatabase.getInstance().getReference().child("CalendarLabtests").child("2021").child(month_s).child(day_s).child("MedicalTests").child(slot[slots]).child("Test"+ String.valueOf(test)).child("Labtests");

                                    if (ref != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String key = ds.getKey();
                            if (key.equalsIgnoreCase("LabTest_"+ name)) {
                                Labtest userObj = ds.getValue(Labtest.class);
                                list.add(userObj);
                                adapterClass.notifyItemInserted(list.size() - 1);
                                recyclerView.setAdapter(adapterClass);
                            }
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewLabtests.this, error.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }


    } }}}

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);

                    return true;
                }
            });

        }

    }


    private void search(String str) {
        ArrayList<Labtest> mylist = new ArrayList<>();
        for (Labtest object : list) {


                if (object.getDate().toLowerCase().contains(str.toLowerCase())) {
                    mylist.add(object);

                    adapterClass.notifyItemInserted(mylist.size() - 1);

                }


            //recyclerView.setAdapter(adapterClass);
            LabtestsAdapter adapterClass = new LabtestsAdapter(mylist, this);
            recyclerView.setAdapter(adapterClass);

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
                startActivity(new Intent(getApplicationContext(),FindPatientLabtests.class));
                finish();
            }
        });
    }




}