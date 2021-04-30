package com.example.medbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class FindPatientLabtests extends AppCompatActivity {

    Spinner spinnerPatient_ID;
    Button scanPatient, confirmPatient;
    Spinner  spinnerMedicalTest, spinnertestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_find_patient);
        toolbar();
        spinnerPatient_ID = (Spinner) findViewById(R.id.spinnerPatient_ID_);
        scanPatient = (Button) findViewById(R.id.scanPatient);

        setPatients();
        confirmPatient = (Button) findViewById(R.id.confirmPatient);
        confirmPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLabtestOptions();
            }


        });


    }

    private void setPatients() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        fDatabaseRoot.child("ActiveAppointments").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> patients = new ArrayList<String>();
                final List<String> doctorIDs = new ArrayList<String>();

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {

                    String patient_Name_ID = addressSnapshot.child("patient_Name_ID").getValue(String.class);


                    if (patient_Name_ID != null) {
                        patients.add(patient_Name_ID);

                    }


                }

                List<String> deDupStringList = new ArrayList<>(new HashSet<>(patients));

                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(FindPatientLabtests.this, android.R.layout.simple_spinner_item, deDupStringList);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPatient_ID.setAdapter(addressAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    public void showLabtestOptions() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_labtest_options, null, false);

        ((Activity) this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        String patient_Name_ID = spinnerPatient_ID.getSelectedItem().toString();
        Button bookLabtest = (Button) dialog.findViewById(R.id.bookLT);
        bookLabtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               bookLabtestDialog(patient_Name_ID);
                //Intent intent1 = new Intent(v.getContext(), BookLabtest.class);
                //intent1.putExtra("patientName",  patient_Name_ID);
                //v.getContext().startActivity(intent1);


            }
        });

        Button uploadResults = (Button) dialog.findViewById(R.id.uploadResults);
        uploadResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(v.getContext(), ViewLabtests.class);
                intent1.putExtra("patientName",  patient_Name_ID);
                v.getContext().startActivity(intent1);

            }
        });

        Button viewResults = (Button) dialog.findViewById(R.id.viewResults);
        viewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(v.getContext(), ViewResults.class);
                intent1.putExtra("patientName",  patient_Name_ID);
                v.getContext().startActivity(intent1);

            }
        });



        dialog.show();
    }

    public void bookLabtestDialog(String patient_Name_ID) {

        final Dialog dialog = new Dialog(FindPatientLabtests.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) FindPatientLabtests.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_book_labtest, null, false);

        ((Activity) FindPatientLabtests.this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        spinnerMedicalTest = (Spinner) dialog.findViewById(R.id.spinnerMedicalTest);
        spinnertestType = (Spinner) dialog.findViewById(R.id.spinnertestType);
        EditText comments = (EditText) dialog.findViewById(R.id.comments);

        setMedicalTests();

        Button chooseDateLabtest = (Button) dialog.findViewById(R.id.chooseDateLabtest);
        chooseDateLabtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int  dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(FindPatientLabtests.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                chooseDateLabtest.setText(day + " / " + (month + 1) + " / " + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        Button confirmLabtest = (Button) dialog.findViewById(R.id.confirmLabtest);
        confirmLabtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bookLabtestAppointment(chooseDateLabtest, comments, patient_Name_ID);
                dialog.dismiss();

            }


        });

        dialog.show();
    }

    public void bookLabtestAppointment(Button dateLabtest, EditText comments, String patient_Name_ID) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("CalendarLabtests");


        String dateMedicalTest = dateLabtest.getText().toString();
        String medicalTest = spinnerMedicalTest.getSelectedItem().toString();
        String testName = spinnertestType.getSelectedItem().toString();
        String commentsLab = comments.getText().toString();

        if (checkEmpty(dateMedicalTest)) {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference uidRef = dataSnapshot.getRef();
                    String[] slot = {"","Cellular And Chemical Analysis", "Diagnostic Imaging", "Measurement", "Vital Signs"};
                    for(int month = 6; month <= 6 ; month++) {
                        String month_s = String.valueOf(month);
                        for(int day = 1; day <= 30 ; day++) {
                            String day_s = String.valueOf(day);
                            String date_t = dataSnapshot.child("2021").child(month_s).child(day_s).child("Date").getValue(String.class);
                            if (date_t.equalsIgnoreCase(dateMedicalTest)){
                                for (int slots = 1; slots <= 5; slots++) {
                                    if (slot[slots].equalsIgnoreCase(medicalTest)) {
                                        for (int test = 1; test <= 4; test++) {
                                            String testValue = dataSnapshot.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("TestName").getValue(String.class);
                                            if (testValue.equalsIgnoreCase(testName)) {
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patient_Name_ID).child("patient_Name_ID").setValue(patient_Name_ID);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patient_Name_ID).child("medical_Test_Type").setValue(medicalTest);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patient_Name_ID).child("test_Name").setValue(testName);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patient_Name_ID).child("comments").setValue(commentsLab);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patient_Name_ID).child("date").setValue(dateMedicalTest);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patient_Name_ID).child("active").setValue("true");
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

    }

    private boolean checkEmpty(String date) {

        if(date.equalsIgnoreCase("DD/MM/YEAR")) {

            AlertDialog alertDialog = new AlertDialog.Builder(FindPatientLabtests.this).create();
            alertDialog.setTitle("Empty Fields");
            alertDialog.setMessage("Please choose a date.");

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(FindPatientLabtests.this, "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.show();
            return false;
        }

        else return true;

    }

    public void setMedicalTests() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        fDatabaseRoot.child("MedicalTests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                final List<String> medicalTest = new ArrayList<String>();

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                    String key = addressSnapshot.getKey();
                    if (key != null) {
                        medicalTest.add(key);
                    }
                }


                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(FindPatientLabtests.this, android.R.layout.simple_spinner_item, medicalTest);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedicalTest.setAdapter(addressAdapter);

                int iCurrentSelection = spinnerMedicalTest.getSelectedItemPosition();
                spinnerMedicalTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if (position == 0) {
                            setTestType(medicalTest.get(0));

                        } else if (position == 1) {

                            setTestType(medicalTest.get(1));
                        } else if (position == 2) {

                            setTestType(medicalTest.get(2));
                        } else if (position == 3) {

                            setTestType(medicalTest.get(3));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    public void setTestType(final String dept) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        fDatabaseRoot.child("MedicalTests").child(dept).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<String> testType = new ArrayList<String>();

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                    String firstName = addressSnapshot.child("TestName").getValue(String.class);

                    if (firstName != null) {
                        testType.add(firstName);
                    }


                }


                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(FindPatientLabtests.this, android.R.layout.simple_spinner_item, testType);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnertestType.setAdapter(addressAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }





}