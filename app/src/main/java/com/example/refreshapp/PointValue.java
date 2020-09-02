package com.example.refreshapp;

public class PointValue {
    float xVal, yVal;

    public PointValue(){}

    public PointValue(float x, float y){
        this.xVal = x;
        this.yVal = y;
    }

    public float getX(){
        return xVal;
    }

    public float getY(){
        return yVal;
    }
}
