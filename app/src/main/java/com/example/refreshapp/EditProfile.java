package com.example.refreshapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{
    private Button buttonUpdateProfile;
    private Button buttonCancel;
    private EditText editTextEditProfileNumber;
    private EditText editTextEditProfileFridgeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        buttonUpdateProfile = findViewById( R.id.buttonEditProfileUpdate );
        buttonCancel = findViewById( R.id.buttonEditProfileCancel );
        editTextEditProfileNumber = findViewById( R.id.editTextProfileMobile );
        editTextEditProfileFridgeNumber = findViewById( R.id.editTextProfileFridgeNumber );

        final UserC userC = (UserC) getApplicationContext();
        userC.setEditProfileIds(this);
        userC.getUserProfile(this);

        buttonUpdateProfile.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonUpdateProfile){
            int err = 0;
            String number = editTextEditProfileNumber.getText().toString().trim();
            String fridgenumber = editTextEditProfileFridgeNumber.getText().toString().trim();

            if (numberOrNot(number) || number.length() != 11) {
                SpannableStringBuilder ssbCustErr = new SpannableStringBuilder("Please enter valid mobile number");
                editTextEditProfileNumber.setError(ssbCustErr);
                err++;
            }
            if (numberOrNot(fridgenumber) || fridgenumber.length() != 11) {
                SpannableStringBuilder ssbCustErr = new SpannableStringBuilder("Please enter valid mobile number");
                editTextEditProfileNumber.setError(ssbCustErr);
                err++;
            }

            final UserC userC = (UserC) getApplicationContext();
            if(err == 0 && userC.updateUserProfile()){
                Toast.makeText(this,"Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

        } else if (v == buttonCancel){
            finish();
        }
    }

    static boolean numberOrNot(String input)
    {
        try
        {
            Integer.parseInt(input);
        }
        catch(NumberFormatException ex)
        {
            return false;
        }
        return true;
    }
}
