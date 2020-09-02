package com.example.refreshapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Power extends AppCompatActivity {
    private UserC userC = new UserC();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        setTitle("Energy Consumption");
    }

    @Override
    protected void onStart(){
        super.onStart();

        userC.setPowerIds(this);
        userC.setPowerListeners(this);
    }
}
