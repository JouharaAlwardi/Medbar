package com.example.medbar.Objects;

public class Result {

String medical_Test_Type, patient_Name_ID, test_Name, test_date;

    public Result() {


    }

    public Result(String medical_Test_Type, String patient_Name_ID, String test_Name, String test_date) {
        this.medical_Test_Type = medical_Test_Type;
        this.patient_Name_ID = patient_Name_ID;
        this.test_Name = test_Name;
        this.test_date = test_date;
    }

    public String getTest_date() {
        return test_date;
    }

    public void setTest_date(String test_date) {
        this.test_date = test_date;
    }

    public String getMedical_Test_Type() {
        return medical_Test_Type;
    }

    public void setMedical_Test_Type(String medical_Test_Type) {
        this.medical_Test_Type = medical_Test_Type;
    }

    public String getPatient_Name_ID() {
        return patient_Name_ID;
    }

    public void setPatient_Name_ID(String patient_Name_ID) {
        this.patient_Name_ID = patient_Name_ID;
    }

    public String getTest_Name() {
        return test_Name;
    }

    public void setTest_Name(String test_Name) {
        this.test_Name = test_Name;
    }
}
