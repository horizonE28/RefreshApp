package com.example.refreshapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;
import java.util.Locale;

public class Bill extends AppCompatActivity {
    String TAG = "Bill";

    private Button buttonSelectMonth;
    private Button buttonAddBill;
    private EditText editTextAmount;

    private int selMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int selYear = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        setTitle("Bills");

        buttonSelectMonth = findViewById(R.id.buttonSelectMonth);
        buttonAddBill = findViewById(R.id.buttonAddBill);
        editTextAmount = findViewById(R.id.editTExtBillAmount);

        final UserC userC = (UserC) getApplicationContext();
        userC.setBillIds(this);
        userC.setBillListeners(this);

        buttonAddBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Bill", buttonSelectMonth.getText().toString());
                Toast.makeText(Bill.this, buttonSelectMonth.getText(), Toast.LENGTH_SHORT);
                if(buttonSelectMonth.getText().toString().equals("Select Month")) {
                    Toast.makeText(Bill.this, "Please select month", Toast.LENGTH_SHORT);
                }
                else{
                    String id = String.valueOf(selYear);
                    if(selMonth < 9){
                        id += "0" + String.valueOf(selMonth+1);
                    }
                    else{
                        id += String.valueOf(selMonth+1);
                    }
                    Float val = Float.parseFloat(editTextAmount.getText().toString());
                    if(userC.setNewBill(id, val)){
                        Toast.makeText(Bill.this, "Bills updated", Toast.LENGTH_SHORT);
                        buttonSelectMonth.setText("Select Month");
                        editTextAmount.setText("");
                    }
                }
            }
        });

        setPicker();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    private void setPicker(){
        final Calendar today = Calendar.getInstance();
        findViewById(R.id.buttonSelectMonth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(
                        Bill.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
//                        Toast.makeText(Bill.this, "Date set with month" + selectedMonth
//                                + " year " + selectedYear, Toast.LENGTH_SHORT).show();

                        selMonth = selectedMonth;
                        selYear = selectedYear;
                        String sel = selMonth+1 + "/" + selYear;
                        buttonSelectMonth.setText(sel);
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                if(selYear == today.get(Calendar.YEAR)){

                }

                builder.setMinYear((today.get(Calendar.YEAR) - 4))
                        .setMaxYear((today.get(Calendar.YEAR)))
                        .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                        .setActivatedYear(selYear)
                        .setActivatedMonth(selMonth)
                        .setTitle("Select month")
                        .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                            @Override
                            public void onMonthChanged(int selectedMonth) {
                                Log.d(TAG, "Selected month : " + selectedMonth);
//                                 Toast.makeText(Bill.this, " Selected month : " +
//                                         selectedMonth, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int selectedYear) {
                                Log.d(TAG, "Selected year : " + selectedYear);
                                if(selectedYear == today.get(Calendar.YEAR)){
                                    builder.setMaxMonth(today.get(Calendar.MONTH));
                                }
                                else{
                                    builder.setMaxMonth(Calendar.DECEMBER);
                                }
                            }
                        })
                        .build()
                        .show();

            }
        });
    }
}
