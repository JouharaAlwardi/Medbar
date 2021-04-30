package com.example.medbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientRecord extends AppCompatActivity {

    EditText fname, lname, dbirth, ephone, Hid, pAddress;
    DatabaseReference ref;
    FirebaseAuth fAuth;

    public static final String MESSAGE_KEY = "MESSAGE1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_record);


        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        dbirth = (EditText) findViewById(R.id.dbirth);
        ephone = (EditText) findViewById(R.id.ephone);
        Hid = (EditText) findViewById(R.id.Hid);
        pAddress = (EditText) findViewById(R.id.pAddress);


        Intent intent = getIntent();
        final String barcode = intent.getStringExtra(MESSAGE_KEY);


        fAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Patients");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {

                    for(DataSnapshot ds : snapshot.getChildren())
                    {

                        String barCode = ds.child("Barcode").getValue(String.class);
                        if (barcode.equalsIgnoreCase(barCode)) {

                            String firstname = ds.child("FirstName").getValue(String.class);
                            String lastname = ds.child("LastName").getValue(String.class);
                            String dateofbirth = ds.child("DateOfBirth").getValue(String.class);
                            String phone = ds.child("PhoneNumber").getValue(String.class);
                            String hid = ds.child("HospitalID").getValue(String.class);
                            String address = ds.child("Address").getValue(String.class);
                            fname.setText(firstname);
                            lname.setText(lastname);
                            dbirth.setText(dateofbirth);
                            ephone.setText(phone);
                            Hid.setText(hid);
                            pAddress.setText(address);

                        }



                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}