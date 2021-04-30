package com.example.medbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.medbar.Objects.Report;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewReportResults extends AppCompatActivity {

    String patinet_Name_ID, test_name, test_type, test_date;
    TextView patient_id_view, patient_test_view, test_date_view, comments_;
    Button viewReport;

    ListView listview;
    DatabaseReference ref;
    List<Report> uploadedPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_report_results);
        toolbar();


        patinet_Name_ID = getIntent().getExtras().getString("patientName");
        test_name = getIntent().getExtras().getString("testName");
        test_type = getIntent().getExtras().getString("testType");
        test_date = getIntent().getExtras().getString("testDate");

        patient_id_view = findViewById(R.id.patient_id_report);
        patient_test_view = findViewById(R.id.patient_test_report);
        test_date_view = findViewById(R.id.test_date_report);
        comments_ = findViewById(R.id.comments_report);

        patient_id_view.setText(patinet_Name_ID);
        patient_test_view.setText(test_name);
        test_date_view.setText(test_date);

        uploadedPDF = new ArrayList<>();
        retrievePDFFiles();

         viewReport = (Button) findViewById(R.id.viewReport);
         viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLabtestDialog();

            }
        });



    }

    public void showLabtestDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_retrieve_pdf, null, false);

        ((Activity) this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        listview = dialog.findViewById(R.id.listView);


        uploadedPDF = new ArrayList<>();
        retrievePDFFiles();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Report pdf = uploadedPDF.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                intent.setData(Uri.parse(pdf.getUrl()));
                startActivity(intent);
            }
        });

        dialog.show();

    }

    public void retrievePDFFiles() {


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
                                            String comments = dataSnapshot.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("Results_report_"+patinet_Name_ID).child("comments").getValue(String.class);
                                            comments_.setText(comments);
                                            comments_.setMovementMethod(new ScrollingMovementMethod());
                                            ref = FirebaseDatabase.getInstance().getReference().child("CalendarLabtests").child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("Results_report_"+patinet_Name_ID).child("report");
                                            if (ref != null && listview != null) {
                                                ref.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {

                                                            for (DataSnapshot ds : snapshot.getChildren()) {


                                                                Report url = ds.getValue(Report.class);
                                                                uploadedPDF.add(url);


                                                                }

                                                            String[] uploadsName = new String[uploadedPDF.size()];
                                                            for(int i = 0; i < uploadsName.length; i++) {
                                                                uploadsName[i] = uploadedPDF.get(i).getUrl();

                                                            }
                                                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploadsName) {
                                                                @NonNull
                                                                @Override
                                                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                                                    View view = super.getView(position, convertView, parent);
                                                                    TextView textView = (TextView) view .findViewById(android.R.id.text1);
                                                                    textView.setTextColor(Color.BLACK);
                                                                    textView.setTextSize(10);
                                                                    return view;

                                                                }
                                                            };

                                                            listview.setAdapter(arrayAdapter);


                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(ViewReportResults.this, error.getMessage(), Toast.LENGTH_LONG).show();

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