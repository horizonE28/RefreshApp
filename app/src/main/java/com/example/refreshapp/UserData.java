package com.example.refreshapp;

public class UserData {
    public String firstName;
    public String lastName;
    public String mobileNumber;
    public String fridgeNumber;
    public float fridgeWattage = 0;
    public float pricePerKWH = 0;

    public UserData(){}

    public UserData(String fname, String lname, String mobile, String fridgeNumber,float fridgeWattage, float pricePerKWH){
        this.firstName = fname;
        this.lastName = lname;
        this.mobileNumber = mobile;
        this.fridgeNumber = fridgeNumber;
        this.fridgeWattage = fridgeWattage;
        this.pricePerKWH = pricePerKWH;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFridgeNumber() {
        return fridgeNumber;
    }

    public void setFridgeNumber(String fridgeNumber) {
        this.fridgeNumber = fridgeNumber;
    }

    public float getFridgeWattage() {
        return fridgeWattage;
    }

    public void setFridgeWattage(float fridgeWattage) {
        this.fridgeWattage = fridgeWattage;
    }

    public float getPricePerKWH() {
        return pricePerKWH;
    }

    public void setPricePerKWH(float pricePerKWH) {
        this.pricePerKWH = pricePerKWH;
    }
}
