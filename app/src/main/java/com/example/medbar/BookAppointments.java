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
import android.graphics.Color;
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
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookAppointments extends AppCompatActivity  {


    TextView textView6,textView8;
    String name, date, doctorID, month_;
    FirebaseAuth fAuth;
    static Spinner deptSpinner, drSpinner, spinnerPatients, spinnerMedicalTest, spinnertestType;
    List<String> propertyAddressList;
    static String currentName;
    Button bookAppointment, bookLabtest, chooseDate, chooseTime;
    Dialog dialogTime;
    Button button9, button10, button113, button1, button2, button4, button430, button630;
    boolean[] flag = {false, false, false, false, false, false, false, false, false};
    boolean fullyBooked = false;
    CheckBox checkButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_book_appointments);


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


        fAuth = FirebaseAuth.getInstance();
        textView6 = (TextView) findViewById(R.id.textView6);
        textView8 = (TextView) findViewById(R.id.textView8);
        deptSpinner = (Spinner) findViewById(R.id.spinnerYear);
        drSpinner = (Spinner) findViewById(R.id.spinnerDr);
        spinnerPatients = (Spinner) findViewById(R.id.spinnerPatients);
        dialogTime = new Dialog(this);
        checkButton = findViewById(R.id.checkButton);
        bookLabtest = (Button) findViewById(R.id.bookLabtest);

        setDoctor();

        setPatients();


        chooseDate = (Button) findViewById(R.id.chooseDate);
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDoctorUID();
            }
        });

        chooseTime = (Button) findViewById(R.id.chooseTime);
        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView6.getText().toString().equalsIgnoreCase("DD/MM/YEAR")) {

                    AlertDialog alertDialog = new AlertDialog.Builder(BookAppointments.this).create();
                    alertDialog.setTitle("Error Message");
                    alertDialog.setMessage("You must choose a date to pick a time slot.");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertDialog.show();
                }
                else {
                    viewTimeslots();
                }
            }
        });

        bookAppointment = (Button) findViewById(R.id.bookAppointment);
        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDoctorUIDBook();

            }


        });

        bookLabtest = (Button) findViewById(R.id.bookLabtest);
        bookLabtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bookLabtestDialog();

            }

        });



    }
    private void getDoctorUIDBook() {

        String text = drSpinner.getSelectedItem().toString();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Doctors");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    //match spinner value
                    String uid = ds.getKey();
                    String firstName = dataSnapshot.child(uid).child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child(uid).child("lastName").getValue(String.class);
                    String doctorID = dataSnapshot.child(uid).child("doctorID").getValue(String.class);
                    String nameDr = "Dr." + firstName + " " + lastName + " - " + doctorID;
                    if (nameDr.equalsIgnoreCase(text)) {
                        makeAppointment(uid);
                        activeAppointments(uid);
                        break;
                    }

                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void makeAppointment(String uid) {


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("Calendar").child(uid);

        String doctorDept = deptSpinner.getSelectedItem().toString();
        String doctorNameID = drSpinner.getSelectedItem().toString();
        String patientNameID = spinnerPatients.getSelectedItem().toString();
        String appointmentDate = textView6.getText().toString();
        String time = textView8.getText().toString();

       if  (checkEmpty(appointmentDate, time)) {

           ValueEventListener valueEventListener = new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   DatabaseReference uidRef = dataSnapshot.getRef();
                   String[] slot = {"","9:00", "10:00", "11:30", "1:00", "2:00","4:00", "4:30","6:30"};
                   for(int month = 6; month <= 8 ; month++) {
                       String month_s = String.valueOf(month);
                       for(int day = 1; day <= 30 ; day++) {
                           String day_s = String.valueOf(day);
                           String date_t = dataSnapshot.child("2021").child(month_s).child(day_s).child("Date").getValue(String.class);
                           if (date_t.equalsIgnoreCase(appointmentDate)){
                           for (int slots = 1; slots <= 8; slots++) {
                               if (slot[slots].equalsIgnoreCase(time)) {
                                   uidRef.child("2021").child(month_s).child(day_s).child("Timeslots").child(slot[slots]).child("booked").setValue("true");
                                   uidRef.child("2021").child(month_s).child(day_s).child("Timeslots").child(slot[slots]).child("Appointments").child("Appointment_"+patientNameID).child("doctor_Dept").setValue(doctorDept);
                                   uidRef.child("2021").child(month_s).child(day_s).child("Timeslots").child(slot[slots]).child("Appointments").child("Appointment_"+patientNameID).child("doctor_Name_ID").setValue(doctorNameID);
                                   uidRef.child("2021").child(month_s).child(day_s).child("Timeslots").child(slot[slots]).child("Appointments").child("Appointment_"+patientNameID).child("patient_Name_ID").setValue(patientNameID);
                                   uidRef.child("2021").child(month_s).child(day_s).child("Timeslots").child(slot[slots]).child("Appointments").child("Appointment_"+patientNameID).child("appointment_Date").setValue(appointmentDate);
                                   uidRef.child("2021").child(month_s).child(day_s).child("Timeslots").child(slot[slots]).child("Appointments").child("Appointment_"+patientNameID).child("appointment_Time").setValue(time);
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

    private void activeAppointments(String uid) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("ActiveAppointments").child(uid);

        String doctorDept = deptSpinner.getSelectedItem().toString();
        String doctorNameID = drSpinner.getSelectedItem().toString();
        String patientNameID = spinnerPatients.getSelectedItem().toString();
        String appointmentDate = textView6.getText().toString();
        String time = textView8.getText().toString();



            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference uidRef = dataSnapshot.getRef();

                    uidRef.child("Appointment_" + patientNameID + "_" + time).child("doctor_Dept").setValue(doctorDept);
                    uidRef.child("Appointment_" + patientNameID + "_" + time).child("doctor_Name_ID").setValue(doctorNameID);
                    uidRef.child("Appointment_" + patientNameID + "_" + time).child("patient_Name_ID").setValue(patientNameID);
                    uidRef.child("Appointment_" + patientNameID + "_" + time).child("appointment_Date").setValue(appointmentDate);
                    uidRef.child("Appointment_" + patientNameID + "_" + time).child("appointment_Time").setValue(time);


            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){
                databaseError.getMessage(); //Don't ignore errors!
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }






    private boolean checkEmpty(String date, String time) {

        if(date.equalsIgnoreCase("DD/MM/YEAR") || time.equalsIgnoreCase("00:00")) {

            AlertDialog alertDialog = new AlertDialog.Builder(BookAppointments.this).create();
            alertDialog.setTitle("Empty Fields");
            alertDialog.setMessage("Please make sure both date and time are not empty.");

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.show();
            return false;
        }

        else return true;

    }


    public void bookLabtestDialog() {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_book_labtest, null, false);

            ((Activity) this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(BookAppointments.this,
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

                    bookLabtestAppointment(chooseDateLabtest, comments);
                    dialog.dismiss();

                }


            });

            dialog.show();
        }


    public void bookLabtestAppointment(Button dateLabtest, EditText comments) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("CalendarLabtests");

        String patientNameID = spinnerPatients.getSelectedItem().toString();
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
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patientNameID).child("patient_Name_ID").setValue(patientNameID);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patientNameID).child("medical_Test_Type").setValue(medicalTest);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patientNameID).child("test_Name").setValue(testName);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patientNameID).child("comments").setValue(commentsLab);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patientNameID).child("date").setValue(dateMedicalTest);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(medicalTest).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patientNameID).child("active").setValue("true");
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

            AlertDialog alertDialog = new AlertDialog.Builder(BookAppointments.this).create();
            alertDialog.setTitle("Empty Fields");
            alertDialog.setMessage("Please choose a date.");

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
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

             
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(BookAppointments.this, android.R.layout.simple_spinner_item, medicalTest);
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

             
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(BookAppointments.this, android.R.layout.simple_spinner_item, testType);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnertestType.setAdapter(addressAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }
    
    
    


    private void getDoctorUID() {

        String text = drSpinner.getSelectedItem().toString();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Doctors");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    //match spinner value
                    String uid = ds.getKey();
                    String firstName = dataSnapshot.child(uid).child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child(uid).child("lastName").getValue(String.class);
                    String doctorID = dataSnapshot.child(uid).child("doctorID").getValue(String.class);
                    String nameDr = "Dr." + firstName + " " + lastName + " - " + doctorID;
                    if (nameDr.equalsIgnoreCase(text)) {
                        viewCalender(uid);
                        break;
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }




    private void setDoctor() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("Doctors").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String department = dataSnapshot.child("department").getValue(String.class);
                String doctorID = dataSnapshot.child("doctorID").getValue(String.class);
                String nameDr = "Dr." + firstName + " " + lastName + " - " + doctorID;

                displaySpinners(department);
                currentName = nameDr;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage();
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);

    }


    private void setPatients() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        fDatabaseRoot.child("Patients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> patients = new ArrayList<String>();
                final List<String> doctorIDs = new ArrayList<String>();

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {

                    String firstName = addressSnapshot.child("FirstName").getValue(String.class);
                    String lastName = addressSnapshot.child("LastName").getValue(String.class);
                    String dID = addressSnapshot.child("HospitalID").getValue(String.class);


                    if (firstName != null) {
                        patients.add( firstName + " " + lastName + " - " + dID);

                    }


                }

                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(BookAppointments.this, android.R.layout.simple_spinner_item, patients);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPatients.setAdapter(addressAdapter);
       ;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void displaySpinners(String department) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        fDatabaseRoot.child("Departments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                final List<String> propertyAddressList = new ArrayList<String>();

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                    String propertyAddress = addressSnapshot.getKey();
                    if (propertyAddress != null) {
                        propertyAddressList.add(propertyAddress);
                    }
                }

                deptSpinner = (Spinner) findViewById(R.id.spinnerYear);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(BookAppointments.this, android.R.layout.simple_spinner_item, propertyAddressList);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                deptSpinner.setAdapter(addressAdapter);
                deptSpinner.setSelection(getIndex(deptSpinner, department));


                int iCurrentSelection = deptSpinner.getSelectedItemPosition();
                deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if (position == 0) {

                            setSpinner(propertyAddressList.get(0));

                        } else if (position == 1) {

                            setSpinner(propertyAddressList.get(1));

                        } else if (position == 2) {

                            setSpinner(propertyAddressList.get(2));

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {

                    }

                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void setSpinner(final String dept) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        fDatabaseRoot.child("Departments").child(dept).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                propertyAddressList = new ArrayList<String>();
                final List<String> doctorIDs = new ArrayList<String>();

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {


                    String firstName = addressSnapshot.child("firstName").getValue(String.class);
                    String lastName = addressSnapshot.child("lastName").getValue(String.class);
                    String dID = addressSnapshot.child("doctorID").getValue(String.class);


                    if (firstName != null) {
                        propertyAddressList.add("Dr." + firstName + " " + lastName + " - " + dID);
                        doctorIDs.add(dID);
                    }


                }


                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(BookAppointments.this, android.R.layout.simple_spinner_item, propertyAddressList);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                drSpinner.setAdapter(addressAdapter);
                drSpinner.setSelection(getIndex(drSpinner, currentName));

                int iCurrentSelection = drSpinner.getSelectedItemPosition();
                drSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                        name = doctorIDs.get(position);
                        checkForLabtests();


                    }



                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {

                    }

                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void checkForLabtests() {

        String txt = drSpinner.getSelectedItem().toString();


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("Doctors").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String doctorID = dataSnapshot.child("doctorID").getValue(String.class);
                String nameDr = "Dr." + firstName + " " + lastName + " - " + doctorID;

                if (nameDr.equalsIgnoreCase(txt)) {

                    checkButton.setChecked(true);
                    checkButton.setEnabled(false);
                    bookLabtest.setVisibility(View.VISIBLE);

                }

                else {

                    checkButton.setChecked(false);
                    checkButton.setEnabled(false);
                    bookLabtest.setVisibility(View.INVISIBLE);

                }






            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage();
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);



    }

    private static int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }


    public void viewCalender(String uid) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_appointment_calender, null, false);

        ((Activity) this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        CalendarView calenderView = (CalendarView) dialog.findViewById(R.id.calendarView);
        final Intent intent= new Intent(BookAppointments.this, BookAppointments.class);
        calenderView.setOnDateChangeListener(new android.widget.CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull android.widget.CalendarView view, int year, int month, int dayOfMonth) {

                date =  dayOfMonth + " / " + (month + 1) + " / " +year;
                month_ = String.valueOf(month+1);

            }

        });


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().getActualMinimum(Calendar.HOUR_OF_DAY));
        long date1 = calendar.getTime().getTime();
        calenderView.setMinDate(date1);

        Button dialogBtn_cancel = (Button) dialog.findViewById(R.id.chooseTimeDate);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    fullyBooked = false;
                    flag = new boolean[]{false, false, false, false, false, false, false, false, false};
                    textView6.setText(date);
                    textView8.setText("00:00");
                    disableBookedDays(month_, uid);
                    dialog.dismiss();

            }
        });

        dialog.show();
    }





    public void viewTimeslots() {

        dialogTime = new Dialog(this);
        dialogTime.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_timeslots, null, false);

        ((Activity) this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogTime.setContentView(view);
        final Window window = dialogTime.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


         button10 = (Button) dialogTime.findViewById(R.id.button10);
         button113 = (Button) dialogTime.findViewById(R.id.button113);
         button9 = (Button) dialogTime.findViewById(R.id.button9);
         button1 = (Button) dialogTime.findViewById(R.id.button1);
         button2 = (Button) dialogTime.findViewById(R.id.button2);
         button4 = (Button) dialogTime.findViewById(R.id.button4);
         button430 = (Button) dialogTime.findViewById(R.id.button430);
         button630 = (Button) dialogTime.findViewById(R.id.button630);


        checkBookings();

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView8.setText("9:00");
                dialogTime.dismiss();
            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView8.setText("10:00");
                dialogTime.dismiss();
            }
        });

        button113.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView8.setText("11:30");
                dialogTime.dismiss();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView8.setText("1:00");
                dialogTime.dismiss();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView8.setText("2:00");
                dialogTime.dismiss();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView8.setText("4:00");
                dialogTime.dismiss();
            }
        });

        button430.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView8.setText("4:30");
                dialogTime.dismiss();
            }
        });

        button630.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView8.setText("6:30");
                dialogTime.dismiss();
            }
        });

        dialogTime.show();

    }

    public void checkBookings() {


        if (flag[1]) {

            button9.setEnabled(false);
            button9.setBackgroundColor(Color.LTGRAY);
            button9.setTextColor(Color.WHITE);

        }

        if (flag[2]) {

            button10.setEnabled(false);
            button10.setBackgroundColor(Color.LTGRAY);
            button10.setTextColor(Color.WHITE);

        }

        if (flag[3]) {

            button113.setEnabled(false);
            button113.setBackgroundColor(Color.LTGRAY);
            button113.setTextColor(Color.WHITE);

        }

        if (flag[4]) {

            button1.setEnabled(false);
            button1.setBackgroundColor(Color.LTGRAY);
            button1.setTextColor(Color.WHITE);

        }

        if (flag[5]) {

            button2.setEnabled(false);
            button2.setBackgroundColor(Color.LTGRAY);
            button2.setTextColor(Color.WHITE);

        }

        if (flag[6]) {

            button4.setEnabled(false);
            button4.setBackgroundColor(Color.LTGRAY);
            button4.setTextColor(Color.WHITE);

        }

        if (flag[7]) {

            button430.setEnabled(false);
            button430.setBackgroundColor(Color.LTGRAY);
            button430.setTextColor(Color.WHITE);

        }

        if (flag[8]) {

            button630.setEnabled(false);
            button630.setBackgroundColor(Color.LTGRAY);
            button630.setTextColor(Color.WHITE);

        }



    }

    public void disableBookedDays(String month, String uid) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        fDatabaseRoot.child("Calendar").child(uid).child("2021").child(month).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {

                    String date_t = addressSnapshot.child("Date").getValue(String.class);
                    if (date.equalsIgnoreCase(date_t)) {
                        String[] slot = {"","9:00", "10:00", "11:30", "1:00", "2:00","4:00", "4:30","6:30"};
                        for (int slots = 1; slots <= 8; slots++) {

                            String booked = addressSnapshot.child("Timeslots").child(slot[slots]).child("booked").getValue(String.class);
                            if (booked.equalsIgnoreCase("true")) {
                                flag[slots] = true;


                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }

        });


    }


    private void showAlert() {

        AlertDialog alertDialog = new AlertDialog.Builder(BookAppointments.this).create();
        alertDialog.setTitle("Fully Booked");
        alertDialog.setMessage("This Day is fully booked. Please pick another day");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();

    }




}
