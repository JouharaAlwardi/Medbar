package com.example.medbar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medbar.Objects.Labtest;
import com.example.medbar.R;
import com.example.medbar.UploadResults;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LabtestsAdapter extends RecyclerView.Adapter<LabtestsAdapter.MyViewHolder>{

    ArrayList<Labtest> list;
    Context context;



    public LabtestsAdapter(ArrayList<Labtest> list, Context context) {

        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.labtest_holder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MyViewHolder holder, final int i) {

        holder.testName.setText(list.get(i).getTest_Name());
        holder.testType.setText(list.get(i).getMedical_Test_Type());
        holder.testDate.setText(list.get(i).getDate());
        String activeTxt =  list.get(i).getActive();
        if ( activeTxt.equalsIgnoreCase("true")) {
            holder.active.setVisibility(View.VISIBLE);
            holder.uploadResults_btn.setVisibility(View.VISIBLE);

        }
        else {

            holder.active.setVisibility(View.INVISIBLE);
            holder.uploadResults_btn.setVisibility(View.INVISIBLE);

        }

        holder.uploadResults_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String patientID = list.get(i).getPatient_Name_ID();
                String testName =list.get(i).getTest_Name();
                String testType = list.get(i).getMedical_Test_Type();
                String testDate = list.get(i).getDate();
                Intent intent1 = new Intent(v.getContext(), UploadResults.class);
                intent1.putExtra("patientName", patientID);
                intent1.putExtra("testName", testName);
                intent1.putExtra("testType", testType);
                intent1.putExtra("testDate", testDate);
                v.getContext().startActivity(intent1);

            }
        });

        holder.cancelLabtests_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                cancelFromCalendar(list.get(i).getPatient_Name_ID(), list.get(i).getTest_Name());


            }
        });
        //holder.doctorID.setText(list.get(i).getDoctorID());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView testName, testType, testDate, active;
        Button uploadResults_btn, cancelLabtests_btn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            testName = itemView.findViewById(R.id.testName);
            testType = itemView.findViewById(R.id.testType);
            testDate = itemView.findViewById(R.id.testDate_);
            active = itemView.findViewById(R.id.active);
            uploadResults_btn = itemView.findViewById(R.id.uploadResults_btn);
            cancelLabtests_btn = itemView.findViewById(R.id.cancelLabtests_btn);

        }
    }
    public void cancelFromCalendar( String patient_Name_ID, String test_name){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("CalendarLabtests");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference uidRef = dataSnapshot.getRef();
                String[] slot = {"","Cellular And Chemical Analysis", "Diagnostic Imaging", "Measurement", "Vital Signs"};
                for(int month = 6; month <= 6 ; month++) {
                    String month_s = String.valueOf(month);
                    for(int day = 1; day <= 5 ; day++) {
                        String day_s = String.valueOf(day);
                        for (int slots = 1; slots <= 4; slots++) {
                            for (int test = 1; test <= 4; test++) {
                                String date_t = dataSnapshot.child("2021").child(month_s).child(day_s).child("MedicalTests").child(slot[slots]).child("Test" + String.valueOf(test)).child("TestName").getValue(String.class);
                                System.out.println(date_t);
                                if (date_t.equalsIgnoreCase(test_name)) {
                                    uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(slot[slots]).child("Test" + String.valueOf(test)).child("Labtests").child("LabTest_" + patient_Name_ID).removeValue();
                                    break;
                                }
                            }}}}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage(); //Don't ignore errors!
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);

    }
}
