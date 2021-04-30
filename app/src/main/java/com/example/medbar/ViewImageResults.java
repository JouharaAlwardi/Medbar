package com.example.medbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medbar.Adapters.ImageResultAdapter;
import com.example.medbar.Adapters.ResultAdapter;
import com.example.medbar.Objects.ImageResult;
import com.example.medbar.Objects.Report;
import com.example.medbar.Objects.Result;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewImageResults extends AppCompatActivity {

    String patinet_Name_ID, test_name, test_type, test_date;
    TextView patient_id_view, patient_test_view, test_date_view, comments_;

    DatabaseReference ref;
    ArrayList<ImageResult> list;
    RecyclerView recyclerView;
    FirebaseAuth fAuth;
    ImageResultAdapter adapterClass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_image_results);
        toolbar();
        patinet_Name_ID = getIntent().getExtras().getString("patientName");
        test_name = getIntent().getExtras().getString("testName");
        test_type = getIntent().getExtras().getString("testType");
        test_date = getIntent().getExtras().getString("testDate");

        patient_id_view = findViewById(R.id.patient_id_view);
        patient_test_view = findViewById(R.id.patient_test_view);
        test_date_view = findViewById(R.id.test_date_view);
        comments_ = findViewById(R.id.comments_);


        patient_id_view.setText(patinet_Name_ID);
        patient_test_view.setText(test_name);
        test_date_view.setText(test_date);


        fAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.image_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        list = new ArrayList<ImageResult>();
        adapterClass = new ImageResultAdapter(list, this);
        recyclerView.setAdapter(adapterClass);


        onstart();



    }


    public void onstart() {


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("CalendarLabtests");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String[] slot = {"","Cellular And Chemical Analysis", "Diagnostic Imaging", "Measurement", "Vital Signs"};
                for(int month = 6; month <= 6 ; month++) {
                    String month_s = String.valueOf(month);
                    for(int day = 1; day <= 30 ; day++) {
                        String day_s = String.valueOf(day);
                        String date_t = dataSnapshot.child("2021").child(month_s).child(day_s).child("Date").getValue(String.class);
                        if (date_t.equalsIgnoreCase(test_date)){
                            for (int slots = 1; slots <= 5; slots++) {
                                if (slot[slots].equalsIgnoreCase(test_type)) {
                                    for (int test = 1; test <= 4; test++) {
                                        String testValue = dataSnapshot.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("TestName").getValue(String.class);
                                        if (testValue.equalsIgnoreCase(test_name)) {
                                            String comments = dataSnapshot.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("Results_images_"+patinet_Name_ID).child("comments").getValue(String.class);
                                            comments_.setText(comments);
                                            comments_.setMovementMethod(new ScrollingMovementMethod());
                                            ref = FirebaseDatabase.getInstance().getReference().child("CalendarLabtests").child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("Results_images_"+patinet_Name_ID).child("images");

                                            if (ref != null) {
                                                ref.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {

                                                            for (DataSnapshot ds : snapshot.getChildren()) {


                                                                String url = ds.child("link").getValue(String.class);
                                                                ImageResult link = new ImageResult(url);
                                                                list.add(link);
                                                                adapterClass.notifyItemInserted(list.size() - 1);
                                                                recyclerView.setAdapter(adapterClass);
                                                            }


                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(ViewImageResults.this, error.getMessage(), Toast.LENGTH_LONG).show();

                                                    }
                                                });
                                            }


                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage(); //Don't ignore errors!
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);


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