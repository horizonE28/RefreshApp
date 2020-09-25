package com.example.refreshapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Temperature extends AppCompatActivity {
    //private UserC userC = new UserC();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        setTitle("Temperature");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final UserC userC = (UserC) getApplicationContext();
        userC.setTemperatureIds(this);
        userC.setTemperatureListeners(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
}
