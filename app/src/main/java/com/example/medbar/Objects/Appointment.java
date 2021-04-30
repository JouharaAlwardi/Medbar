package com.example.medbar.Objects;

public class Appointment {

    String appointment_Date, appointment_Time, doctor_Dept, doctor_Name_ID, patient_Name_ID;

    public Appointment() {
    }

    public Appointment(String appointment_Date, String appointment_Time, String doctor_Dept, String doctor_Name_ID, String patient_Name_ID) {
        this.appointment_Date = appointment_Date;
        this.appointment_Time = appointment_Time;
        this.doctor_Dept = doctor_Dept;
        this.doctor_Name_ID = doctor_Name_ID;
        this.patient_Name_ID = patient_Name_ID;
    }

    public String getAppointment_Date() {
        return appointment_Date;
    }

    public String getAppointment_Time() {
        return appointment_Time;
    }

    public String getDoctor_Dept() {
        return doctor_Dept;
    }

    public String getDoctor_Name_ID() {
        return doctor_Name_ID;
    }

    public String getPatient_Name_ID() {
        return patient_Name_ID;
    }

    public void setAppointment_Date(String appointment_Date) {
        this.appointment_Date = appointment_Date;
    }

    public void setAppointment_Time(String appointment_Time) {
        this.appointment_Time = appointment_Time;
    }

    public void setDoctor_Dept(String doctor_Dept) {
        this.doctor_Dept = doctor_Dept;
    }

    public void setDoctor_Name_ID(String doctor_Name_ID) {
        this.doctor_Name_ID = doctor_Name_ID;
    }

    public void setPatient_Name_ID(String patient_Name_ID) {
        this.patient_Name_ID = patient_Name_ID;
    }
}
