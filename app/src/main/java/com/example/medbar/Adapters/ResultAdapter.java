package com.example.medbar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medbar.Objects.Labtest;
import com.example.medbar.Objects.Result;
import com.example.medbar.R;
import com.example.medbar.UploadResults;
import com.example.medbar.ViewImageResults;
import com.example.medbar.ViewReportResults;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<com.example.medbar.Adapters.ResultAdapter.MyViewHolder> {

    ArrayList<Result> list;
    Context context;


    public ResultAdapter(ArrayList<Result> list, Context context) {

        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public com.example.medbar.Adapters.ResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_holder, parent, false);
        return new com.example.medbar.Adapters.ResultAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.medbar.Adapters.ResultAdapter.MyViewHolder holder, final int i) {

        holder.testName_rs.setText(list.get(i).getTest_Name());
        holder.testType_rs.setText(list.get(i).getMedical_Test_Type());


        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String patientID = list.get(i).getPatient_Name_ID();
                String testName = list.get(i).getTest_Name();
                String testType = list.get(i).getMedical_Test_Type();
                String testDate = list.get(i).getTest_date();
                Intent intent1 = new Intent(v.getContext(), ViewImageResults.class);
                intent1.putExtra("patientName", patientID);
                intent1.putExtra("testName", testName);
                intent1.putExtra("testType", testType);
                intent1.putExtra("testDate", testDate);
                v.getContext().startActivity(intent1);

            }
        });

        holder.reports.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String patientID = list.get(i).getPatient_Name_ID();
                String testName = list.get(i).getTest_Name();
                String testType = list.get(i).getMedical_Test_Type();
                String testDate = list.get(i).getTest_date();
                Intent intent1 = new Intent(v.getContext(), ViewReportResults.class);
                intent1.putExtra("patientName", patientID);
                intent1.putExtra("testName", testName);
                intent1.putExtra("testType", testType);
                intent1.putExtra("testDate", testDate);
                v.getContext().startActivity(intent1);


            }
        });

        holder.delete_result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                cancelFromCalendar(list.get(i).getPatient_Name_ID(), list.get(i).getTest_Name());


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView testName_rs, testType_rs;
        Button images, reports, delete_result;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            testName_rs = itemView.findViewById(R.id.testName_rs);
            testType_rs = itemView.findViewById(R.id.testType_rs);
            images = itemView.findViewById(R.id.images);
            reports = itemView.findViewById(R.id.reports);
            delete_result = itemView.findViewById(R.id.delete_result);


        }
    }




    public void cancelFromCalendar( String patient_Name_ID, String test_name) {

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
                                    uidRef.child("2021").child(month_s).child(day_s).child("MedicalTests").child(slot[slots]).child("Test" + String.valueOf(test)).child("Results").child("Results_" + patient_Name_ID).removeValue();
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
