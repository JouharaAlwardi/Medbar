package com.example.medbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medbar.Objects.Doctors;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignUp extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText nameText, nameText1, doctoridText, emailText, passwordText;

    FirebaseDatabase rootNode;
    DatabaseReference referance;
    FirebaseAuth fAuth;
    Button register;
    Button loginPage;
    String[] depart = {"cardiology", "orthopaedics", "radiology"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);


        nameText = (EditText) findViewById(R.id.name);
        nameText1 = (EditText) findViewById(R.id.name1);
        doctoridText = (EditText) findViewById(R.id.doctorIDProfile);

        emailText = (EditText) findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);

        fAuth = FirebaseAuth.getInstance();
        final String[] dept = new String[2];

        // if (fAuth.getCurrentUser() != null) {

        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //startActivity(intent);

        //}

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        fDatabaseRoot.child("Departments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                final List<String> propertyAddressList = new ArrayList<String>();

                for (DataSnapshot addressSnapshot: dataSnapshot.getChildren()) {
                    String propertyAddress = addressSnapshot.getKey();
                    if (propertyAddress!=null){
                        propertyAddressList.add(propertyAddress);
                    }
                }

                Spinner spinnerProperty = (Spinner) findViewById(R.id.spinnerDeptartments);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(SignUp.this, android.R.layout.simple_spinner_item, propertyAddressList);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProperty.setAdapter(addressAdapter);

                int iCurrentSelection = spinnerProperty.getSelectedItemPosition();
                spinnerProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                        dept[0] = propertyAddressList.get(position);

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




        register = (Button) findViewById(R.id.start
        );
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameText.getText().toString().trim();
                final String lastname = nameText1.getText().toString().trim();
                final String doctorid = doctoridText.getText().toString().trim();
                final String email = emailText.getText().toString().trim();
                final String password = passwordText.getText().toString().trim();


                //Validation
                if (doctorid.length() < 6 || password.length() < 6) {

                    Toast.makeText(SignUp.this, "Doctor ID or password is less than 6 characters", Toast.LENGTH_LONG).show();



                }  if (validatePasswrod(password) == true) {

                    Toast.makeText(SignUp.this, "Whitespace not allowed in password ", Toast.LENGTH_LONG).show();

                }  if (TextUtils.isEmpty(email)) {
                    emailText.setError("Email is Required");
                    return;

                }  if (TextUtils.isEmpty(password)) {
                    passwordText.setError("Password is Required");
                    return;

                }

                else {

                    fAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){

                                        FirebaseUser fuser = fAuth.getCurrentUser();
                                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUp.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                            }
                                        });


                                        Doctors user = new Doctors(name, lastname, doctorid, dept[0],email, password);
                                        String refDept = "";

                                        switch(dept[0]) {
                                            case "cardiology":
                                                refDept = "cardiology";
                                                break;
                                            case "orthopaedics" :
                                                refDept = "orthopaedics";
                                                break;
                                            case "radiology" :
                                                refDept = "radiology";
                                                break;
                                            default :
                                                System.out.println("Invalid");
                                        }

                                        FirebaseDatabase.getInstance().getReference("Departments").child(refDept)
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                }
                                                else {

                                                }
                                            }
                                        });


                                        FirebaseDatabase.getInstance().getReference("Doctors")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    createCalendar(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    Toast.makeText(SignUp.this, "User Created ", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(intent);

                                                }
                                                else {
                                                    Toast.makeText(SignUp.this, "Error ", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });




                                    }
                                }
                            });

                }


            }



        });

        loginPage = (Button) findViewById(R.id.loginPage
        );
        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                loginPage(v);

            }
        });


    }

    public void createCalendar(String uid) {


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("Calendar").child(uid);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference uidRef = dataSnapshot.getRef();
                String[] slot = {"","9:00", "10:00", "11:30", "1:00", "2:00","4:00", "4:30","6:30"};
                for(int month = 6; month <= 8 ; month++) {
                    String month_s = String.valueOf(month);
                    String monthName = findMonth(month_s);
                    uidRef.child("2021").child(month_s).child("Month").setValue("August");
                    for(int day = 1; day <= 30 ; day++) {
                        String day_s = String.valueOf(day);
                        uidRef.child("2021").child(month_s).child(day_s).child("Day").setValue(day_s);
                        uidRef.child("2021").child(month_s).child(day_s).child("Date").setValue(day_s+" / "+month_s+" / "+"2021");
                        for(int slots = 1; slots <= 8 ; slots++) {
                            uidRef.child("2021").child(month_s).child(day_s).child("Timeslots").child(slot[slots]).child("time").setValue(slot[slots]);
                            uidRef.child("2021").child(month_s).child(day_s).child("Timeslots").child(slot[slots]).child("booked").setValue("false");
                        }

                    }
                    uidRef.child("2021").child(month_s).child("Month").setValue(monthName);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage(); //Don't ignore errors!
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);




    }

    public String findMonth(String month) {
        switch (month) {
            case "6":
                month = "June";
                break;
            case "7":
                month = "July";
                break;
            case "8":
                month = "August";
                break;
        }
        return month;
    }


    public boolean validatePasswrod(String pas) {
        boolean valid = true;

        for (int i = 0; i < pas.length(); i++) {

            char ch = pas.charAt(i);
            if (ch == ' ') {

                valid = true;
                break;

            } else {

                valid = false;

            }

        }
        return valid;

    }

    private void loginPage(View v) {

        Intent intent1 = new Intent(SignUp.this, Login.class);
        startActivity(intent1);
    }

}