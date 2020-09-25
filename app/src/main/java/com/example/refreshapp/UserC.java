package com.example.refreshapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.service.autofill.DateValueSanitizer;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserC extends Application {
    private UserData userData;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference fridgeDataRef;
    private DatabaseReference fridgeStatusRef;
    private DatabaseReference userDataRef;
    private DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private DateTimeFormatter dtDispFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
    private DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
    private Query query;

    //Dashboard Views
    private TextView textViewFreezerV;
    private TextView textViewChillerV;
    private TextView textViewCrisperV;
    private TextView textViewCurrentV;
    private TextView textViewPMonthBillV;
    private TextView textViewRefPctV;
//    private EditText editTextBillInput;

    //Temperature Views
    private LineChart tempChart;
    private LineDataSet dataSetFreezer;
    private LineDataSet dataSetChiller;
    private LineDataSet dataSetCrisper;
    private TextView textViewTempLastRec;
    private TextView textViewTFreezerVal;
    private TextView textViewTChillerVal;
    private TextView textViewTCrisperVal;
    private TextView textViewTOverallVal;
    private Switch switchPowerStatus;

    //Power Views
    private LineChart powerChart;
    private LineDataSet dataSetCurrent;
    private TextView textViewPowerLastRec;
    private TextView textViewPowerAvgCurrent;
    private TextView textViewPowerTimesOpened;
    private TextView textViewPowerConsumption;

    //Bill Views
    private BarChart billChart;
    private BarDataSet dataSetBill;
    private TextView textViewAvgBill;
    private TextView textViewAvgFrCon;

    //Edit Profile Views
    private TextView textViewEditProfileName;
    private TextView textViewEditProfileEmail;
    private EditText editTextMobile;
    private EditText editTextFridgeWattage;
    private EditText editTextPricePerKWH;
    private EditText editTextEditProfileFridgeNumber;

    //  default constructor
    public UserC(){}

    /* Get firebase instances*/
    public void initialize(){
        try{
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            if(firebaseUser != null){
                firebaseDatabase = FirebaseDatabase.getInstance();
                fridgeDataRef = firebaseDatabase.getReference("FridgeData/" + firebaseUser.getUid());
                fridgeStatusRef = firebaseDatabase.getReference("FridgeStatus/" + firebaseUser.getUid());
                userDataRef = firebaseDatabase.getReference("UserData/" + firebaseUser.getUid());
            }
        }catch (Exception e){ e.printStackTrace(); }
    }

    public void setDashboardIds(final Activity a){
        try {
            textViewChillerV = a.findViewById(R.id.textViewChillerV);
            textViewCrisperV = a.findViewById(R.id.textViewCrisperV);
            textViewFreezerV = a.findViewById(R.id.textViewFreezerV);
            textViewCurrentV = a.findViewById(R.id.textViewCurrentV);
            switchPowerStatus = a.findViewById(R.id.switchPowerStatus);
            textViewPMonthBillV = a.findViewById(R.id.textViewPMonthBillV);
            //textViewCMonthBillV = a.findViewById(R.id.textViewCMonthBillV);
            textViewRefPctV = a.findViewById(R.id.textViewRefPctV);
            //editTextBillInput = a.findViewById(R.id.editTextBillInput);
        }catch (Exception e){ e.printStackTrace(); }}

    public void setDashboardListeners(final Context c){
        try {
            query = fridgeDataRef.orderByKey().limitToLast(1);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //  update readings

                        FData data = postSnapshot.getValue(FData.class);

                        if (data != null) {
                            textViewChillerV.setText(String.format("%.2f", data.chiller_val) + " °C");
                            textViewFreezerV.setText(String.format("%.2f", data.freezer_val) + " °C");
                            textViewCrisperV.setText(String.format("%.2f", data.crisper_val) + " °C");
                            textViewCurrentV.setText(String.format("%.2f", data.current_val) + " mA");
                        } else{
                            textViewChillerV.setText(c.getString(R.string.noData));
                            textViewFreezerV.setText(c.getString(R.string.noData));
                            textViewCrisperV.setText(c.getString(R.string.noData));
                            textViewCurrentV.setText(c.getString(R.string.noData));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(c, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            query = fridgeStatusRef.orderByKey().limitToLast(4);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FridgeStatus prev = null;
                    String prevKey = "";

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if(!snapshot.getKey().equals("PowerStatus") && !snapshot.getKey().equals("ConnStatus")){
                            prev = snapshot.getValue(FridgeStatus.class);
                            prevKey = snapshot.getKey();
                        }
                    }
                    if(prev != null) {
                        String bill = String.valueOf(prev.electricityBill) + ", " + getMonthYear(prevKey);
                        textViewPMonthBillV.setText(bill);
                        textViewRefPctV.setText(String.valueOf(prev.fridgePercentage) + "%");
                    }
                    else{
                        textViewPMonthBillV.setText(c.getString(R.string.noValue));
                        textViewRefPctV.setText(c.getString(R.string.noValue));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(c, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){ e.printStackTrace(); }
    }

    public void setSwitchListener(Activity a){
        fridgeStatusRef.child("PowerStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                switchPowerStatus.setChecked( dataSnapshot.getValue(Boolean.TYPE) );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setPowerStatus(boolean status){
        //update firebase
        fridgeStatusRef.child("PowerStatus").setValue(status);
    }

    public void setTemperatureIds(final Activity a){
        textViewTempLastRec = a.findViewById( R.id.textViewTempLastRec );
        textViewTChillerVal = a.findViewById( R.id.textViewTChillerVal );
        textViewTCrisperVal = a.findViewById( R.id.textViewTCrisperVal );
        textViewTFreezerVal = a.findViewById( R.id.textViewTFreezerVal );
        textViewTOverallVal = a.findViewById( R.id.textViewTOverallVal );
        tempChart = a.findViewById( R.id.lineChartTemp );
        Description d = new Description();
        d.setText(" ");
        tempChart.setAutoScaleMinMaxEnabled(false);
        tempChart.setDescription(d);
    }

    public void setTemperatureListeners(final Context c){
        //graph listeners
        query = fridgeDataRef.limitToLast(25);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    float avgFreezer = 0, avgChiller = 0, avgCrisper = 0;
                    int childCount = (int) dataSnapshot.getChildrenCount();

                    if(childCount > 0){
                        ArrayList<Entry> freezerDat = new ArrayList<>();
                        ArrayList<Entry> chillerDat = new ArrayList<>();
                        ArrayList<Entry> crisperDat = new ArrayList<>();
                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                        int indx = 0;

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            FData data = ds.getValue(FData.class);
                            LocalDateTime dt = LocalDateTime.parse(data.timestamp, datetimeFormatter);
//                            long t = sdf.parse(data.timestamp).getTime();
//                            long d = TimeUnit.MILLISECONDS.toHours(t);
                            long x = localDateTimeToDate(dt).getTime();

                            freezerDat.add(new Entry(indx, data.freezer_val));
                            chillerDat.add(new Entry(indx, data.chiller_val));
                            crisperDat.add(new Entry(indx, data.crisper_val));

                            avgFreezer += data.freezer_val;
                            avgChiller += data.chiller_val;
                            avgCrisper += data.crisper_val;

                            if(indx == childCount-1){
                                String lastrec = c.getString(R.string.lastrec) + " ";
                                textViewTempLastRec.setText( lastrec.concat(dt.format(dtDispFormat)));
                            }

                            indx++;
                        }

                        avgFreezer /= childCount;
                        avgChiller /= childCount;
                        avgCrisper /= childCount;

                        textViewTChillerVal.setText(String.format("%.2f", avgChiller) + " °C");
                        textViewTCrisperVal.setText(String.format("%.2f", avgCrisper) + " °C");
                        textViewTFreezerVal.setText(String.format("%.2f", avgFreezer) + " °C");

                        float avg = (avgChiller + avgCrisper + avgFreezer) / 3;
                        textViewTOverallVal.setText(String.format("%.2f", avg) + " °C");

                        dataSetFreezer = new LineDataSet(freezerDat, "Freezer");
                        dataSetFreezer.setDrawCircleHole(false);
                        dataSetFreezer.setColor(Color.rgb(114,147,203));
                        dataSetFreezer.setCircleColors(Color.rgb(114,147,203));
                        dataSetFreezer.setLineWidth(2);
                        dataSetFreezer.setCircleHoleRadius(2);
                        dataSetFreezer.setDrawValues(false);

                        dataSetChiller = new LineDataSet(chillerDat, "Chiller");
                        dataSetChiller.setDrawCircleHole(false);
                        dataSetChiller.setColor(Color.rgb(218,124,48));
                        dataSetChiller.setCircleColors(Color.rgb(218,124,48));
                        dataSetChiller.setLineWidth(2);
                        dataSetChiller.setCircleHoleRadius(2);
                        dataSetChiller.setDrawValues(false);

                        dataSetCrisper = new LineDataSet(crisperDat, "Crisper");
                        dataSetCrisper.setDrawCircleHole(false);
                        dataSetCrisper.setColor(Color.rgb(62,150,81));
                        dataSetCrisper.setCircleColors(Color.rgb(62,150,81));
                        dataSetCrisper.setLineWidth(2);
                        dataSetCrisper.setCircleHoleRadius(2);
                        dataSetCrisper.setDrawValues(false);

                        dataSets.add(dataSetFreezer);
                        dataSets.add(dataSetChiller);
                        dataSets.add(dataSetCrisper);

                        tempChart.setData(new LineData(dataSets));
                        tempChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        tempChart.getAxisRight().setEnabled(false);
                        tempChart.getXAxis().setAxisLineColor(Color.WHITE);
                        tempChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        tempChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

                            @Override
                            public String getFormattedValue(float value) {
                                long millis = TimeUnit.HOURS.toMillis((long) value);
//                                return mFormat.format(new Date(millis));
                                    return  "";
                            }});
                        tempChart.animateX(2000, Easing.EaseInBack);
                    }

                }catch (Exception e){ e.printStackTrace(); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(c, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setPowerIds(final Activity a){
        textViewPowerLastRec = a.findViewById( R.id.textViewPowerLastRec );
        textViewPowerAvgCurrent = a.findViewById( R.id.textViewPCurrVal );
        textViewPowerTimesOpened = a.findViewById( R.id.textViewPReedCount );
        textViewPowerConsumption = a.findViewById( R.id.textViewPPowerConsumption );

        powerChart = a.findViewById( R.id.lineChartPower );
        Description d = new Description();
        d.setText(" ");
        powerChart.setAutoScaleMinMaxEnabled(false);
        powerChart.setDescription(d);
    }

    public void setPowerListeners(final Context c){
        query = fridgeDataRef.limitToLast(50);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int childCount = (int) dataSnapshot.getChildrenCount();
                    float avgCurrent = 0;

                    if (childCount > 0) {
                        ArrayList<Entry> currentDat = new ArrayList<>();
                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                        int indx = 0;

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            FData data = ds.getValue(FData.class);
                            LocalDateTime dt = LocalDateTime.parse(data.timestamp, datetimeFormatter);
                            Date x = localDateTimeToDate(dt);

                            currentDat.add(new Entry(indx, data.current_val));
                            avgCurrent += data.current_val;

                            if (indx == childCount - 1) {
                                String lastrec = c.getString(R.string.lastrec) + " ";
                                textViewPowerLastRec.setText(lastrec.concat(dt.format(dtDispFormat)));
                            }
                            indx++;
                        }

                        avgCurrent /= childCount;

                        textViewPowerAvgCurrent.setText(String.format("%.2f", avgCurrent) + " A");

                        dataSetCurrent = new LineDataSet(currentDat, "Current(A)");
                        dataSetCurrent.setDrawCircleHole(false);
                        dataSetCurrent.setColor(Color.rgb(218,124,48));
                        dataSetCurrent.setCircleColors(Color.rgb(218,124,48));
                        dataSetCurrent.setLineWidth(4);
                        dataSetCurrent.setFillColor(Color.rgb(218,124,48));
                        dataSetCurrent.setFillAlpha(95);
                        dataSetCurrent.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        dataSetCurrent.setDrawFilled(true);
                        dataSetCurrent.setDrawValues(false);

                        dataSets.add(dataSetCurrent);

                        powerChart.setData(new LineData(dataSets));
                        powerChart.getAxisLeft().setDrawGridLines(false);
                        powerChart.getAxisRight().setEnabled(false);
                        powerChart.getXAxis().setAxisLineColor(Color.WHITE);
                        powerChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        powerChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

                            @Override
                            public String getFormattedValue(float value) {

                                long millis = TimeUnit.HOURS.toMillis((long) value);
//                                return mFormat.format(new Date(millis));
                                return "";
                            }});
                        powerChart.getXAxis().setLabelCount(5);
                        powerChart.animateX(2500);
                    }
                }
                catch (Exception e){ e.printStackTrace(); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(c, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        query = fridgeStatusRef.limitToLast(2);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        if(ds.getKey().compareTo("PowerStatus") != 0){
                            FridgeStatus fs = ds.getValue(FridgeStatus.class);

                            textViewPowerConsumption.setText(Float.toString(fs.powerConsumed) + " W");
                            textViewPowerTimesOpened.setText(Integer.toString(fs.timesOpened));
                            break;
                        }
                    }
                }
                catch (Exception e){ e.printStackTrace(); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setBillIds(final Activity a){
        billChart = a.findViewById( R.id.barChartBill );
        Description d = new Description();
        d.setText(" ");
        billChart.setAutoScaleMinMaxEnabled(false);
        billChart.setDescription(d);

        textViewAvgBill = a.findViewById( R.id.textViewAvgBill );
        textViewAvgFrCon = a.findViewById( R.id.textViewAvgFridgeCont );
    }

    public void setBillListeners(final Context c){
        query = fridgeStatusRef.limitToLast(14);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    int childCount = (int) dataSnapshot.getChildrenCount();
                    if (childCount > 0){
                        int indx = 0;
                        float ref = 0;
                        float avgBill = 0;
                        float avgFr = 0;
                        int[] colors = new int[] {Color.rgb(62,150,81), Color.rgb(114,147,203)};
                        final ArrayList<String> months = new ArrayList<>();
                        final int[] monthlist = new int[12];
                        months.add("Jan");
                        months.add("Feb");
                        months.add("Mar");
                        months.add("Apr");
                        months.add("May");
                        months.add("Jun");
                        months.add("Jul");
                        months.add("Aug");
                        months.add("Sep");
                        months.add("Oct");
                        months.add("Nov");
                        months.add("Dec");

                        ArrayList<BarEntry> billEntries = new ArrayList<>();
                        ArrayList<IBarDataSet> dataset = new ArrayList<>();

                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            if(ds.getKey().compareTo("PowerStatus") != 0 && ds.getKey().compareTo("ConnStatus") != 0) {
                                FridgeStatus fs = ds.getValue(FridgeStatus.class);
                                monthlist[indx] = getMonth(ds.getKey());

                                float fBill = fs.electricityBill * (fs.fridgePercentage/100);
                                billEntries.add(new BarEntry(indx, new float[]{fBill, fs.electricityBill}));

                                avgBill += fs.electricityBill;
                                avgFr += fBill;
                                indx++;
                            }
                        }
                        avgBill /= (childCount-2);
                        avgFr /= (childCount-2);

                        textViewAvgBill.setText(String.format("Php %.2f", avgBill));
                        textViewAvgFrCon.setText(String.format("Php %.2f", avgFr));

                        dataSetBill = new BarDataSet(billEntries, " ");
                        dataSetBill.setColors(colors);
                        dataSetBill.setStackLabels(new String[]{"Fridge Contribution", "Total Bill"});
                        dataset.add(dataSetBill);

                        billChart.setData(new BarData(dataset));
                        billChart.getAxisLeft().setDrawGridLines(false);
                        billChart.getXAxis().setDrawGridLines(false);
                        billChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
                        billChart.setHighlightFullBarEnabled(true);
                        billChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                return months.get((int)monthlist[(int)value]);
                                }});
                        billChart.animateX(1500, Easing.EaseInQuad);
                    }
                }
                catch (Exception e){ e.printStackTrace(); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean setNewBill(String id, float electricityBill){
        try{
            fridgeStatusRef.child(id).child("electricityBill").setValue(electricityBill);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void setEditProfileIds(final Activity a){
        textViewEditProfileName = a.findViewById( R.id.textViewEditProfileName );
        textViewEditProfileEmail = a.findViewById( R.id.textViewEditProfileEmail );
        editTextMobile = a.findViewById( R.id.editTextProfileMobile );
        editTextFridgeWattage = a.findViewById( R.id.editTextProfileFridgeWattage );
        editTextPricePerKWH = a.findViewById( R.id.editTextProfilePrice );
        editTextEditProfileFridgeNumber = a.findViewById( R.id.editTextProfileFridgeNumber );
    }

    public void getUserProfile(final Context c){
        userDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    userData = dataSnapshot.getValue(UserData.class);

                    if(userData != null){
                        textViewEditProfileName.setText("Name: " + userData.firstName + " " + userData.lastName);
                        textViewEditProfileEmail.setText("Email: " + firebaseUser.getEmail());
                        editTextMobile.setText(userData.mobileNumber);
                        editTextFridgeWattage.setText(Float.toString(userData.fridgeWattage));
                        editTextPricePerKWH.setText(Float.toString(userData.pricePerKWH));
                        editTextEditProfileFridgeNumber.setText(userData.fridgeNumber);
                    }
                }
                catch (Exception e){ e.printStackTrace(); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean updateUserProfile(){
        try{
            userData.mobileNumber = editTextMobile.getText().toString();
            userData.fridgeWattage = Float.parseFloat(editTextFridgeWattage.getText().toString());
            userData.pricePerKWH = Float.parseFloat(editTextPricePerKWH.getText().toString());
            userData.fridgeNumber = editTextEditProfileFridgeNumber.getText().toString();
            userDataRef.setValue(userData);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private int getMonth(String s){
        return Integer.parseInt(s.substring(4)) - 1;
    }

    private String getMonthYear(String s){
        String retVal = "";

        switch (s.substring(2)){
            case "01":
                retVal = "Jan";
                break;

            case "02":
                retVal = "Feb";
                break;

            case "03":
                retVal = "Mar";
                break;

            case "04":
                retVal = "Apr";
                break;

            case "05":
                retVal = "May";
                break;

            case "06":
                retVal = "Jun";
                break;

            case "07":
                retVal = "Jul";
                break;

            case "08":
                retVal = "Aug";
                break;

            case "09":
                retVal = "Sept";
                break;

            case "10":
                retVal = "Oct";
                break;

            case "11":
                retVal = "Nov";
                break;

            case "12":
                retVal = "Dec";
                break;
        }
        return retVal;
    }

    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(0, 0, 0,
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar.getTime();
    }
}