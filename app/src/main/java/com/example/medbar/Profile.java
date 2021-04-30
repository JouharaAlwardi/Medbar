package com.example.medbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    CircleImageView profileImage;
    EditText nameDB, nameDB2, phoneDB, emailProfile, addressProfile, dobProfile, doctorIDProfile, pwsProfile;
    Button update;
    FirebaseAuth fAuth;
    DatabaseReference reference;
    String nameDBSt, nameDB2St, phoneDBSt, emailProfileSt, addressProfileSt, dobProfileSt, doctorIDProfileSt, pwsProfileSt;
    Button changeAvater;
    String dept,gender;
    StorageReference storageReferance;
    Uri image;
    Spinner egender;
    private Spinner spinner;
    String[] paths = {"Female", "Male", "Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);
        toolbar();



        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_profile,true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                switch (i) {
                    case R.id.bottom_nav_dashboard:
                        Intent intent = new Intent(Profile.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.bottom_nav_scan:
                        Intent intent1 = new Intent(Profile.this, BarcodeScan.class);
                        startActivity(intent1);
                        break;
                    case R.id.bottom_nav_profile:
                        Intent intent2 = new Intent(Profile.this, Profile.class);
                        startActivity(intent2);
                        break;
                }

            }
        });

        fAuth = FirebaseAuth.getInstance();

        nameDB = findViewById(R.id.nameDB);
        nameDB2 = findViewById(R.id.nameDB2);
        phoneDB = findViewById(R.id.phoneDB);
        pwsProfile = findViewById(R.id.pwsProfile);
        emailProfile = findViewById(R.id.emailProfile);
        addressProfile = findViewById(R.id.addressProfile);
        dobProfile = findViewById(R.id.dobProfile);
        doctorIDProfile = findViewById(R.id.doctorIDProfile);
        profileImage = findViewById(R.id.profileImage);
        changeAvater = findViewById(R.id.changeImage);

        Intent intent = getIntent();
        nameDBSt = intent.getStringExtra("firstName");
        nameDB2St = intent.getStringExtra("lastName");
        phoneDBSt = intent.getStringExtra("phoneNumber");
        pwsProfileSt = intent.getStringExtra("password");
        emailProfileSt = intent.getStringExtra("email");
        addressProfileSt = intent.getStringExtra("address");
        dobProfileSt = intent.getStringExtra("dateOfBirth");
        doctorIDProfileSt = intent.getStringExtra("doctorID");

        storageReferance = FirebaseStorage.getInstance().getReference();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("Doctors").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nameText = dataSnapshot.child("firstName").getValue(String.class);
                String lnameText = dataSnapshot.child("lastName").getValue(String.class);
                String phoneText = dataSnapshot.child("phoneNumber").getValue(String.class);
                String passwordTxt = dataSnapshot.child("password").getValue(String.class);
                String emailText = dataSnapshot.child("email").getValue(String.class);
                String addressTxt = dataSnapshot.child("address").getValue(String.class);
                String dobText = dataSnapshot.child("dateOfBirth").getValue(String.class);
                String doctorIDText = dataSnapshot.child("doctorID").getValue(String.class);
                dept = dataSnapshot.child("department").getValue(String.class);
                String image = dataSnapshot.child("imageURL").getValue(String.class);

                nameDB.setText(nameText);
                nameDB2.setText(lnameText);
                phoneDB.setText(phoneText);
                pwsProfile.setText(passwordTxt);
                emailProfile.setText(emailText);
                addressProfile.setText(addressTxt);
                dobProfile.setText(dobText);
                doctorIDProfile.setText(doctorIDText);
                Picasso.get().load(image).into(profileImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage(); //Don't ignore errors!
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);


        update = (Button) findViewById(R.id.updateData);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                updateData(v);
                updateDepartment(v);

            }
        });


        changeAvater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1000);


            }
        });




        egender = (Spinner) findViewById(R.id.gender_spinner);
        ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_spinner_item, paths);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        egender.setAdapter(addressAdapter);

        int iCurrentSelection = egender.getSelectedItemPosition();
        egender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    gender = "Female";

                } else if (position == 1) {

                    gender = "Male";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {

            if(resultCode == Activity.RESULT_OK) {

                Uri imageUri = data.getData();
                profileImage.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);



            }
        }

    }

    public void uploadImageToFirebase(Uri imageUri) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference fileref = storageReferance.child(uid);
        image = imageUri;


        fileref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Profile.this, "Image Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });


    }


    public void updateData(View view) {



        if (image.toString().matches("")) {
            Toast.makeText(this, "Please Enter Add an Image", Toast.LENGTH_SHORT).show();

        }    else {

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference uidRef = rootRef.child("Doctors").child(uid);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    DatabaseReference uidRef = dataSnapshot.getRef();
                    uidRef.child("firstName").setValue(nameDB.getText().toString().trim());
                    uidRef.child("lastName").setValue(nameDB2.getText().toString());
                    uidRef.child("phoneNumber").setValue(phoneDB.getText().toString());
                    uidRef.child("password").setValue(pwsProfile.getText().toString());
                    uidRef.child("email").setValue(emailProfile.getText().toString());
                    uidRef.child("address").setValue(addressProfile.getText().toString());
                    uidRef.child("dateOfBirth").setValue(dobProfile.getText().toString());
                    uidRef.child("doctorID").setValue(doctorIDProfile.getText().toString());
                    uidRef.child("imageURL").setValue(image.toString());
                    uidRef.child("gender").setValue(gender);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.getMessage(); //Don't ignore errors!
                }
            };
            uidRef.addListenerForSingleValueEvent(valueEventListener);

            Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();
        }

    }


    public void updateDepartment(View view) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("Departments").child(dept).child(uid);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DatabaseReference uidRef = dataSnapshot.getRef();
                uidRef.child("firstName").setValue(nameDB.getText().toString().trim());
                uidRef.child("lastName").setValue(nameDB2.getText().toString());
                uidRef.child("phoneNumber").setValue(phoneDB.getText().toString());
                uidRef.child("password").setValue(pwsProfile.getText().toString());
                uidRef.child("email").setValue(emailProfile.getText().toString());
                uidRef.child("address").setValue(addressProfile.getText().toString());
                uidRef.child("dateOfBirth").setValue(dobProfile.getText().toString());
                uidRef.child("doctorID").setValue(doctorIDProfile.getText().toString());
                uidRef.child("imageURL").setValue(image.toString());
                uidRef.child("gender").setValue(gender);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage(); //Don't ignore errors!
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);

        Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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