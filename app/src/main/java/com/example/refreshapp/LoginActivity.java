package com.example.refreshapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextFName;
    private EditText editTextLName;
    private EditText editTextEmail;
    private EditText editTextMobile;
    private EditText editTextPassword;
    private EditText editTextVPassword;
    private EditText editTextFridgeWattage;
    private EditText editTextPricePerKWH;
    private EditText editTextFridgeNumber;
    private Button buttonRegister;
    private TextView textViewSignin;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            //start dashboard
            finish();
            startActivity(new Intent(this, DashboardActivity.class));
        }

        findViews();
        progressDialog = new ProgressDialog(this);
    }

    private void findViews() {
        editTextFName = findViewById( R.id.editTextFName );
        editTextLName = findViewById( R.id.editTextLName );
        editTextEmail = findViewById( R.id.editTextEmail );
        editTextMobile = findViewById( R.id.editTextMobile );
        editTextPassword = findViewById( R.id.editTextPassword );
        editTextVPassword = findViewById( R.id.editTextVPassword );
        editTextFridgeWattage = findViewById( R.id.editTextFridgeWattage );
        editTextPricePerKWH = findViewById( R.id.editTextPricePerKWH );
        editTextFridgeNumber = findViewById( R.id.editTextFridgeNumber );
        buttonRegister = findViewById( R.id.buttonRegister );
        textViewSignin = findViewById( R.id.textViewSignin );

        buttonRegister.setOnClickListener( this );
        textViewSignin.setOnClickListener( this );
    }

    private void registerUser(){
        try {
            String fname = editTextFName.getText().toString().trim();
            String lname = editTextLName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String mobile = editTextMobile.getText().toString().trim();
            String fridgemobile = editTextFridgeNumber.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String vpassword = editTextVPassword.getText().toString().trim();
            String fridgeWattage = editTextFridgeWattage.getText().toString().trim();
            String pricePerKWH = editTextPricePerKWH.getText().toString().trim();

            String errString = "This field cannot be empty";

            //  Set error display
            ForegroundColorSpan fgColorSpan = new ForegroundColorSpan(getColor(R.color.errorColor));
            SpannableStringBuilder ssbError = new SpannableStringBuilder(errString);
            SpannableStringBuilder ssbCustErr;
            ssbError.setSpan(fgColorSpan, 0, errString.length(), 0);
            int err = 0;

            if (TextUtils.isEmpty(fname)) {
                editTextFName.setError(ssbError);
                err++;
            } else if (!isAlpha(fname)) {
                ssbCustErr = new SpannableStringBuilder("Please enter valid name");
                editTextFName.setError(ssbCustErr);
                err++;
                return;
            }
            if (TextUtils.isEmpty(fname)) {
                editTextLName.setError(ssbError);
                err++;
            } else if (!isAlpha(lname)) {
                ssbCustErr = new SpannableStringBuilder("Please enter valid name");
                editTextLName.setError(ssbCustErr);
                err++;
                return;
            }
            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError(ssbError);
                err++;
            }
            if (TextUtils.isEmpty(mobile)) {
                editTextMobile.setError(ssbError);
                err++;
            } else if (numberOrNot(mobile) || mobile.length() != 11) {
                ssbCustErr = new SpannableStringBuilder("Please enter valid mobile number");
                editTextMobile.setError(ssbCustErr);
                err++;
            }
            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError(ssbError);
                err++;
            } else if (password.length() < 8) {
                ssbCustErr = new SpannableStringBuilder("Password should be at least 8 characters long");
                editTextPassword.setError(ssbCustErr);
                err++;
            } else if (password.compareTo(vpassword) != 0) {
                ssbCustErr = new SpannableStringBuilder("Password does not match");
                editTextVPassword.setError(ssbCustErr);
                err++;
            }
            if (TextUtils.isEmpty(fridgeWattage)) {
                editTextFridgeWattage.setError(ssbError);
                err++;
            }
            if (TextUtils.isEmpty(pricePerKWH)) {
                editTextPricePerKWH.setError(ssbError);
                err++;
            }
            if (numberOrNot(fridgemobile) || fridgemobile.length() != 11) {
                ssbCustErr = new SpannableStringBuilder("Please enter valid fridge number");
                editTextFridgeNumber.setError(ssbCustErr);
                err++;
            }

            if (err < 1) {
                userData = new UserData(fname, lname, mobile, fridgemobile, Float.parseFloat(fridgeWattage), Float.parseFloat(pricePerKWH));
                //  Display message to user
                progressDialog.setMessage("Registering User...");
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();

                                if (task.isSuccessful()) {
                                    //user authentication registered
                                    Toast.makeText(LoginActivity.this,
                                            "Registered Successfully",
                                            Toast.LENGTH_SHORT).show();

                                    //add user data to database
                                    DatabaseReference dbr = FirebaseDatabase.getInstance().getReference();

                                    try {
                                        dbr.child("UserData").child(mAuth.getCurrentUser().getUid()).setValue(userData);
                                        dbr.child("FridgeStatus").child(mAuth.getCurrentUser().getUid()).child("PowerStatus").setValue(false);
                                        finish();
                                        startActivity(new Intent(getApplicationContext(),
                                                DashboardActivity.class));
                                    }
                                    catch (Exception e){
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Could not register. Please try again",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } catch ( Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister){
            registerUser();
        } else if (view == textViewSignin){
            //open login activity
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
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
