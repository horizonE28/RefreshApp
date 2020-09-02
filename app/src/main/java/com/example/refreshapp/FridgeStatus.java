package com.example.refreshapp;

public class FridgeStatus {
    public float electricityBill;
    public float fridgePercentage;
    public float powerConsumed;
    public int timesOpened;

    //  Default constructor
    public FridgeStatus(){

    }

    public float getElectricityBill() {
        return electricityBill;
    }

    public void setElectricityBill(float electricityBill) {
        this.electricityBill = electricityBill;
    }

    public float getFridgePercentage() {
        return fridgePercentage;
    }

    public void setFridgePercentage(float fridgePercentage) {
        this.fridgePercentage = fridgePercentage;
    }

    public float getPowerConsumed() {
        return powerConsumed;
    }

    public void setPowerConsumed(float powerConsumed) {
        this.powerConsumed = powerConsumed;
    }

    public int getTimesOpened() {
        return timesOpened;
    }

    public void setTimesOpened(int timesOpened) {
        this.timesOpened = timesOpened;
    }
}
