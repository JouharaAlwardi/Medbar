package com.example.medbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.medbar.Adapters.ResultAdapter;
import com.example.medbar.Objects.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class UploadResults extends AppCompatActivity {

    String patinet_Name_ID, test_name, test_type, test_date, comments;
    Button pdfReports, imagesResults, choose_rs_rp,upload_rs_rp,choose_rs, upload_rs;
    ImageButton dismissBtn;
    TextView alert, alert_rp;
    StorageReference storageReference_rp;
    DatabaseReference databaseReference_rp, databaseReference;
    EditText annotations;

    private int uploads = 0;
    private ProgressDialog progressDialog;
    int index = 0;

    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private static final int PICK_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_upload_results);
        //Get data from adapter
        toolbar();
        patinet_Name_ID = getIntent().getExtras().getString("patientName");
        test_name = getIntent().getExtras().getString("testName");
        test_type = getIntent().getExtras().getString("testType");
        test_date = getIntent().getExtras().getString("testDate");

        pdfReports = (Button) findViewById(R.id.pdfReports);
        pdfReports.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                showPDFDialog();


            }


        });

        imagesResults = (Button) findViewById(R.id.imagesResult);
        imagesResults.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showImageDialog();


            }
        });




    }


    public void showPDFDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.upload_reports, null, false);

        ((Activity) this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        progressDialog = new ProgressDialog(this);

        alert_rp = dialog.findViewById(R.id.alert_rp);
        TextView patient_id = (TextView) dialog.findViewById(R.id.patient_id_rp);
        TextView patient_test = (TextView) dialog.findViewById(R.id.patient_test_rp);
        patient_id.setText(patinet_Name_ID);
        patient_test.setText(test_name);

         annotations = (EditText) dialog.findViewById(R.id.annotations_rp);


        ImageButton dismissBtn = (ImageButton) dialog.findViewById(R.id.dismissBtn_rp);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

         storageReference_rp = FirebaseStorage.getInstance().getReference();
         databaseReference_rp = FirebaseDatabase.getInstance().getReference("UploadPDF");

        upload_rs_rp= (Button) dialog.findViewById(R.id.upload_rs_rp);
        choose_rs_rp = (Button) dialog.findViewById(R.id.choose_rs_rp);
        choose_rs_rp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectReport();
            }
        });


        dialog.show();

    }

     Dialog dialogImage;


    public void showImageDialog() {

        dialogImage = new Dialog(this);
        dialogImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.upload_images, null, false);

        ((Activity) this).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogImage.setContentView(view);
        final Window window = dialogImage.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Results");
        progressDialog.setMessage("Uploading ..........");
        alert = dialogImage.findViewById(R.id.alert);
        TextView patient_id = (TextView) dialogImage.findViewById(R.id.patient_id);
        TextView patient_test = (TextView) dialogImage.findViewById(R.id.patient_test);
        patient_id.setText(patinet_Name_ID);
        patient_test.setText(test_name);

         annotations = (EditText) dialogImage.findViewById(R.id.annotations);


        choose_rs = dialogImage.findViewById(R.id.choose_rs);
        upload_rs = dialogImage.findViewById(R.id.upload_rs);
        dismissBtn = (ImageButton) dialogImage.findViewById(R.id.dismissBtn);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImage.dismiss();
            }
        });



        dialogImage.show();


    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentImageSelect = 0;

                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        ImageList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }
                    alert.setVisibility(View.VISIBLE);
                    alert.setText("You Have Selected "+ ImageList.size() +" Pictures" );
                    choose_rs.setVisibility(View.GONE);
                }

            }

        }

        if (requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            choose_rs_rp.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
            alert_rp.setText("You Have Selected A Report" );
            choose_rs_rp.setVisibility(View.GONE);
            upload_rs_rp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDFFileFirebase(data.getData());
                }
            });
        }

    }

    private void uploadPDFFileFirebase(Uri data) {

        alert_rp.setText("Please Wait ... If Uploading takes Too much time please press the button again ");
        progressDialog.setTitle("File is loading ...");
        progressDialog.show();
        StorageReference ref =  storageReference_rp.child("UploadPDF"+ System.currentTimeMillis() + "pdf");
        ref.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri = uriTask.getResult();
                        addResults(String.valueOf(uri), "report");

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                double progress = 100.0 * (snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("File Loading ... " + (int) progress + "%");

            }
        });


    }

    private void SendLinkPDF(String url, DatabaseReference newRef) {
   /* SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("m","ReportUploaded");
                editor.apply();*/
        Report putPDF = new Report(url);
        newRef.push().setValue(putPDF).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                upload_rs_rp.setVisibility(View.GONE);
                alert_rp.setText("Report Uploaded Successfully");
                progressDialog.dismiss();
                pdfReports.setEnabled(false);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void upload(View view) {

        alert.setText("Please Wait ... If Uploading takes Too much time please press the button again ");
        progressDialog.setTitle("File is loading ...");
        progressDialog.show();
        final StorageReference ImageFolder =  FirebaseStorage.getInstance().getReference().child("ImageFolder");
        for (uploads=0; uploads < ImageList.size(); uploads++) {
            Uri Image  = ImageList.get(uploads);
            final StorageReference imagename = ImageFolder.child("image/"+Image.getLastPathSegment());

            imagename.putFile(ImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            addResults(url, "images");

                        }
                    });

                }
            });


        }


    }

    public void chooseImages(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMG);

    }
    public void selectReport(){


        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"), 12);


    }

    private void SendLink(String url, DatabaseReference newRef) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);

        newRef.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               /* SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("m","ImageUploaded");
                editor.apply();*/
                progressDialog.dismiss();
                alert.setText("Results Uploaded Successfully");
                upload_rs.setVisibility(View.GONE);
                ImageList.clear();
                imagesResults.setEnabled(false);
            }
        });


    }



    public void addResults(String url, String type) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("CalendarLabtests");

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
                            if (date_t.equalsIgnoreCase(test_date)){
                                for (int slots = 1; slots <= 5; slots++) {
                                    if (slot[slots].equalsIgnoreCase(test_type)) {
                                        for (int test = 1; test <= 4; test++) {
                                            String testValue = dataSnapshot.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("TestName").getValue(String.class);
                                            if (testValue.equalsIgnoreCase(test_name)) {
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Labtests").child("LabTest_"+patinet_Name_ID).child("active").setValue("false");
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("patient_Name_ID").setValue(patinet_Name_ID);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("medical_Test_Type").setValue(test_type);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("test_Name").setValue(test_name);
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("test_Date").setValue(test_date);
                                                DatabaseReference newRef = FirebaseDatabase.getInstance().getReference().child("CalendarLabtests").child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("Results_"+type+"_"+patinet_Name_ID).child(type);
                                                comments = annotations.getText().toString();
                                                uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(test_type).child("Test"+ String.valueOf(test)).child("Results").child("Results_"+patinet_Name_ID).child("Results_"+type+"_"+patinet_Name_ID).child("comments").setValue(comments);
                                                if (type.equalsIgnoreCase("images")) {
                                                    SendLink(url, newRef);
                                                }
                                               else if (type.equalsIgnoreCase("report")) {
                                                    SendLinkPDF(url, newRef);
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
                exitAlert();

            }
        });
    }


    public void exitAlert() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Please make sure you add all the files you need for your reports.");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(getApplicationContext(),FindPatientLabtests.class));
                        finish();



                    }
                });
        builder1.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }



}