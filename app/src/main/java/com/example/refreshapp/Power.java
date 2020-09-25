package com.example.refreshapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Power extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        setTitle("Energy Consumption");

        final UserC userC = (UserC) getApplicationContext();
        userC.setPowerIds(this);
        userC.setPowerListeners(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
}
