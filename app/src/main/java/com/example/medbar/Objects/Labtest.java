package com.example.medbar.Objects;

public class Labtest {

    String comments,date,medical_Test_Type,patient_Name_ID, test_Name, active;

    public Labtest() {
    }

    public Labtest(String comments, String date, String medical_Test_Type, String patient_Name_ID, String test_Name, String active) {
        this.comments = comments;
        this.date = date;
        this.medical_Test_Type = medical_Test_Type;
        this.patient_Name_ID = patient_Name_ID;
        this.test_Name = test_Name;
        this.active = active;
    }



    public void setActive(String active) {
        this.active = active;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMedical_Test_Type(String medical_Test_Type) {
        this.medical_Test_Type = medical_Test_Type;
    }

    public void setPatient_Name_ID(String patient_Name_ID) {
        this.patient_Name_ID = patient_Name_ID;
    }

    public void setTest_Name(String test_Name) {
        this.test_Name = test_Name;
    }

    public String getComments() {
        return comments;
    }

    public String getDate() {
        return date;
    }

    public String getMedical_Test_Type() {
        return medical_Test_Type;
    }

    public String getPatient_Name_ID() {
        return patient_Name_ID;
    }

    public String getTest_Name() {
        return test_Name;
    }

    public String getActive() {

        return active;

    }
}
