package com.example.medbar.Objects;

public class Doctors {

    String Address, DateOfBirth, Department, DoctorID, Email, FirstName, Gender, LastName, PhoneNumber, Password, Avater;

    public Doctors(String Address, String DateOfBirth, String Department, String DoctorID, String Email, String FirstName, String Gender, String LastName, String PhoneNumber, String Password, String Avater) {
        this.Address = Address;
        this.DateOfBirth = DateOfBirth;
        this.Department = Department;
        this.DoctorID = DoctorID;
        this.Email = Email;
        this.FirstName = FirstName;
        this.Gender = Gender;
        this.LastName = LastName;
        this.PhoneNumber = PhoneNumber;
        this.Password = Password;
        this.Avater = Avater;
    }

    public Doctors(String FirstName, String LastName, String DoctorID, String Department, String Email, String Password) {


        this.Department = Department;
        this.DoctorID = DoctorID;
        this.Email = Email;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Password = Password;


    }

    public String getAddress() {
        return this.Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String DateOfBirth) {
        this.DateOfBirth = DateOfBirth;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String Department) {
        this.Department = Department;
    }

    public String getDoctorID() {
        return DoctorID;
    }

    public void setDoctorID(String DoctorID) {
        this.DoctorID = DoctorID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getAvater() {
        return Avater;
    }

    public void setAvater(String Avater) {
        this.Avater = Avater;
    }
}
