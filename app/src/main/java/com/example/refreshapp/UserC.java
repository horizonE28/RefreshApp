package com.example.refreshapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class UserC {
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
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM");
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
    private GraphView graphTemp;
    private LineGraphSeries seriesFreezer;
    private LineGraphSeries seriesChiller;
    private LineGraphSeries seriesCrisper;
    private TextView textViewTempLastRec;
    private TextView textViewTFreezerVal;
    private TextView textViewTChillerVal;
    private TextView textViewTCrisperVal;
    private TextView textViewTOverallVal;
    private Switch switchPowerStatus;

    //Power Views
    private GraphView graphPower;
    private LineGraphSeries seriesCurrent;
    private LineGraphSeries seriesPower;
    private TextView textViewPowerLastRec;
    private TextView textViewPowerAvgCurrent;
    private TextView textViewPowerTimesOpened;
    private TextView textViewPowerConsumption;

    //Bill Views
    private GraphView graphBill;
    private BarGraphSeries seriesBill;
    private BarGraphSeries seriesFridge;

    //Edit Profile Views
    private TextView textViewEditProfileName;
    private TextView textViewEditProfileEmail;
    private EditText editTextMobile;
    private EditText editTextFridgeWattage;
    private EditText editTextPricePerKWH;
    private EditText editTextEditProfileFridgeNumber;

    //  default constructor
    public UserC(){
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
                        //Date d = new SimpleDateFormat("yyyyyMM").parse(prevKey);
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
        graphTemp = a.findViewById( R.id.graphTemps );

        seriesChiller = new LineGraphSeries();
        seriesChiller.setTitle("Chiller");
        seriesChiller.setColor(Color.rgb(218,124,48));
        seriesChiller.setDrawDataPoints(true);
        seriesChiller.setDataPointsRadius(4);
        seriesChiller.setThickness(5);

        seriesFreezer = new LineGraphSeries();
        seriesFreezer.setTitle("Freezer");
        seriesFreezer.setColor(Color.rgb(114,147,203));
        seriesFreezer.setDrawDataPoints(true);
        seriesFreezer.setDataPointsRadius(4);
        seriesFreezer.setThickness(5);

        seriesCrisper = new LineGraphSeries();
        seriesCrisper.setTitle("Crisper");
        seriesCrisper.setColor(Color.rgb(62,150,81));
        seriesCrisper.setDrawDataPoints(true);
        seriesCrisper.setDataPointsRadius(4);
        seriesCrisper.setThickness(5);

        graphTemp.addSeries(seriesFreezer);
        graphTemp.addSeries(seriesChiller);
        graphTemp.addSeries(seriesCrisper);

        graphTemp.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graphTemp.getGridLabelRenderer().setVerticalAxisTitle("Temperature (°C)");
        graphTemp.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphTemp.getGridLabelRenderer().setNumHorizontalLabels(7);
        graphTemp.getGridLabelRenderer().setNumVerticalLabels(7);

        graphTemp.getViewport().setMinY(5);
        graphTemp.getViewport().setMaxY(30);
        graphTemp.getViewport().setYAxisBoundsManual(true);
        graphTemp.getViewport().setXAxisBoundsManual(true);
        graphTemp.getViewport().setScrollable(true);
        graphTemp.getViewport().setScrollableY(true);
        graphTemp.getViewport().setScalable(true);
//        graphTemp.getViewport().setScalableY(true);

        graphTemp.getLegendRenderer().setVisible(true);
        graphTemp.getLegendRenderer().setFixedPosition(2,0);
    }

    public void setTemperatureListeners(final Context c){
        //graph listeners
        query = fridgeDataRef.limitToLast(50);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    long maxT = 0, minT = 0;
                    float maxTemp = 0, minTemp = 100;
                    float avgFreezer = 0, avgChiller = 0, avgCrisper = 0;
                    int childCount = (int) dataSnapshot.getChildrenCount();

                    if(childCount > 0){
                        DataPoint[] dpFreezer = new DataPoint[childCount];
                        DataPoint[] dpChiller = new DataPoint[childCount];
                        DataPoint[] dpCrisper = new DataPoint[childCount];
                        int indx = 0;

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            FData data = ds.getValue(FData.class);
                            LocalDateTime dt = LocalDateTime.parse(data.timestamp, datetimeFormatter);
                            Date x = localDateTimeToDate(dt);

                            dpFreezer[indx] = new DataPoint(x, data.freezer_val);
                            dpChiller[indx] = new DataPoint(x, data.chiller_val);
                            dpCrisper[indx] = new DataPoint(x, data.crisper_val);

                            avgFreezer += data.freezer_val;
                            avgChiller += data.chiller_val;
                            avgCrisper += data.crisper_val;

                            //  set graph min and max values
                            if(indx == 29){
                                minT = x.getTime();
                                minTemp = data.freezer_val;
                            }
                            if(indx == childCount-1){
                                //graphTemp.getViewport().setMaxX(x.getTime());
                                maxT = x.getTime();
                                String lastrec = c.getString(R.string.lastrec) + " ";
                                textViewTempLastRec.setText( lastrec.concat(dt.format(dtDispFormat)));
                            }

                            if(maxTemp < data.freezer_val){
                                maxTemp = data.freezer_val;
                            } else if(maxTemp < data.chiller_val){
                                maxTemp = data.chiller_val;
                            } else if(maxTemp < data.crisper_val){
                                maxTemp = data.crisper_val;
                            }

                            if(minTemp > data.freezer_val){
                                minTemp = data.freezer_val;
                            } else if(minTemp > data.chiller_val){
                                minTemp = data.chiller_val;
                            } else if(minTemp > data.crisper_val){
                                minTemp = data.crisper_val;
                            }

                            indx++;
                        }

                        double offset = (.5 * (maxT - minT) / (childCount));

                        avgFreezer /= childCount;
                        avgChiller /= childCount;
                        avgCrisper /= childCount;

                        graphTemp.getViewport().setMinX(minT - offset);
                        graphTemp.getViewport().setMaxX(maxT + offset);
                        graphTemp.getViewport().setMinY(minTemp - 0.5);
                        graphTemp.getViewport().setMaxY(maxTemp + 0.5);

                        textViewTChillerVal.setText(String.format("%.2f", avgChiller) + " °C");
                        textViewTCrisperVal.setText(String.format("%.2f", avgCrisper) + " °C");
                        textViewTFreezerVal.setText(String.format("%.2f", avgFreezer) + " °C");

                        float avg = (avgChiller + avgCrisper + avgFreezer) / 3;
                        textViewTOverallVal.setText(String.format("%.2f", avg) + " °C");

                        seriesFreezer.resetData(dpFreezer);
                        seriesChiller.resetData(dpChiller);
                        seriesCrisper.resetData(dpCrisper);
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
        graphPower = a.findViewById( R.id.graphPower );

        seriesCurrent = new LineGraphSeries();
        seriesCurrent.setTitle("Current (A)");
        seriesCurrent.setColor(Color.rgb(218,124,48));
        seriesCurrent.setDrawDataPoints(true);
        seriesCurrent.setDataPointsRadius(8);
        seriesCurrent.setThickness(5);
        seriesCurrent.setDrawBackground(true);
        seriesCurrent.setBackgroundColor(Color.rgb(255, 169, 99));

        seriesPower = new LineGraphSeries();
        seriesPower.setTitle("Power Consumption (W)");
        seriesPower.setColor(Color.rgb(218,124,48));
        seriesPower.setDrawDataPoints(true);
        seriesPower.setDataPointsRadius(8);
        seriesPower.setThickness(5);

        //graphPower.addSeries(seriesPower);
        graphPower.addSeries(seriesCurrent);

        graphPower.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graphPower.getGridLabelRenderer().setVerticalAxisTitle("Readings");
        graphPower.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphPower.getGridLabelRenderer().setNumHorizontalLabels(7);
        graphPower.getGridLabelRenderer().setNumVerticalLabels(7);
//        graphPower.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
//            @Override
//            public String formatLabel(double value, boolean isValueX) {
//                if(isValueX)
//                    return sdf.format((long) value);
//
//                return null;
//            }
//
//            @Override
//            public void setViewport(Viewport viewport) {
//
//            }
//        });
//        graphPower.getGridLabelRenderer().setHumanRounding(false);

        graphPower.getViewport().setYAxisBoundsManual(true);
        graphPower.getViewport().setXAxisBoundsManual(true);
        graphPower.getViewport().setScrollable(true);
        graphPower.getViewport().setScrollableY(true);
        graphPower.getViewport().setScalable(true);
//        graphPower.getViewport().setScalableY(true);

        graphPower.getLegendRenderer().setVisible(true);
        graphPower.getLegendRenderer().setFixedPosition(2,0);

    }

    public void setPowerListeners(final Context c){
        query = fridgeDataRef.limitToLast(50);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int childCount = (int) dataSnapshot.getChildrenCount();
                    long maxT = 0, minT = 0;
                    float maxC = 0, minC = 100;
                    float avgCurrent = 0;


                    if (childCount > 0) {
                        DataPoint[] dpCurrent = new DataPoint[childCount];
                        int indx = 0;

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            FData data = ds.getValue(FData.class);
                            LocalDateTime dt = LocalDateTime.parse(data.timestamp, datetimeFormatter);
                            Date x = localDateTimeToDate(dt);

                            dpCurrent[indx] = new DataPoint(x, data.current_val);

                            avgCurrent += data.current_val;

                            //  set graph min and max values
                            if (indx == 29) {
                                minT = x.getTime();
                            }
                            if (indx == childCount - 1) {
                                //graphTemp.getViewport().setMaxX(x.getTime());
                                maxT = x.getTime();
                                String lastrec = c.getString(R.string.lastrec) + " ";
                                textViewPowerLastRec.setText(lastrec.concat(dt.format(dtDispFormat)));
                            }

                            if(maxC < data.current_val){
                                maxC = data.current_val;
                            }
                            if(minC > data.current_val){
                                minC = data.current_val;
                            }

                            indx++;
                        }

                        double offset = (.5 * (maxT - minT) / (childCount - 1));

                        avgCurrent /= childCount;

                        graphPower.getViewport().setMinX(minT - offset);
                        graphPower.getViewport().setMaxX(maxT + offset);
                        graphPower.getViewport().setMinY(0);
                        graphPower.getViewport().setMaxY(maxC + 3);

                        textViewPowerAvgCurrent.setText(String.format("%.2f", avgCurrent) + " A");

                        seriesCurrent.resetData(dpCurrent);
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
        graphBill = a.findViewById( R.id.graphBill );

        seriesBill = new BarGraphSeries();
        seriesBill.setSpacing(20);
        seriesBill.setDrawValuesOnTop(true);
        seriesBill.setTitle("Bills");
        seriesBill.setValuesOnTopColor(seriesBill.getColor());

        seriesFridge = new BarGraphSeries();
        seriesFridge.setSpacing(20);
        seriesFridge.setDrawValuesOnTop(true);
        seriesFridge.setColor(Color.rgb(62,150,81));
        seriesFridge.setValuesOnTopColor(Color.rgb(62,150,81));
        seriesFridge.setTitle("Fridge Contribution");

        graphBill.addSeries(seriesBill);
        graphBill.addSeries(seriesFridge);

        graphBill.getGridLabelRenderer().setHorizontalAxisTitle("Month");
        graphBill.getGridLabelRenderer().setVerticalAxisTitle("Bills");
        graphBill.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graphBill.getGridLabelRenderer().setNumHorizontalLabels(5);
        graphBill.getGridLabelRenderer().setNumVerticalLabels(7);
        graphBill.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                try {
                    if (isValueX && value > 1) {
                        Date d = new Date((long)value);
                        return sdf.format(d);
//                        String s = Double.toString(value);
//                        if (s.length() >= 6) {
//                            s = s.substring(0, 6);
//                            Date d = sdf2.parse(s);
//                            return sdf.format(d);
//                        } else {
//                            return super.formatLabel(0, isValueX);
//                        }
                    } else {
                        return super.formatLabel(value, isValueX);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        graphBill.getViewport().setYAxisBoundsManual(false);
        graphBill.getViewport().setXAxisBoundsManual(true);
        graphBill.getViewport().setScrollable(true);
        graphBill.getViewport().setScrollableY(false);
        graphBill.getViewport().setScalable(true);

        graphBill.getLegendRenderer().setVisible(true);
        graphBill.getLegendRenderer().setTextColor(Color.WHITE);
        graphBill.getLegendRenderer().setFixedPosition(graphBill.getGraphContentWidth(),0);
//        graphBill.getLegendRenderer().setTextSize(12);
    }

    public void setBillListeners(final Context c){
        query = fridgeStatusRef.limitToLast(14);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    int childCount = (int) dataSnapshot.getChildrenCount();
                    double lowest = 0, highest = 0;
                    if (childCount > 0){
                        DataPoint[] dpBills = new DataPoint[childCount-2];
                        DataPoint[] dpFridge = new DataPoint[childCount-2];
                        int indx = 0;

                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            if(ds.getKey().compareTo("PowerStatus") != 0 && ds.getKey().compareTo("ConnStatus") != 0) {
                                FridgeStatus fs = ds.getValue(FridgeStatus.class);
                                double key = Double.valueOf(ds.getKey());
                                long d = sdf2.parse(ds.getKey()).getTime();
//                                long d = sdf2.parse(ds.getKey()).getMonth();
                                dpBills[indx] = new DataPoint(d, fs.electricityBill);
                                dpFridge[indx] = new DataPoint(d, (fs.electricityBill * (fs.fridgePercentage/100)));

                                if(indx == 0){
                                    lowest = d;
                                } else if (indx == childCount - 3){
                                    highest = d;
                                }

                                indx++;

                            }
                        }
                        seriesBill.resetData(dpBills);
                        seriesFridge.resetData(dpFridge);

                        graphBill.getViewport().setMinX(lowest - ((highest - lowest)/childCount-1));
                        graphBill.getViewport().setMaxX(highest +  ((highest - lowest)/childCount-1));
                        //graphBill.getGridLabelRenderer().setNumHorizontalLabels(childCount);
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

    private String getMonthYear(String s){
        String retVal = "";

        switch (s.substring(4)){
            case "01":
                retVal = "Jan ";
                break;

            case "02":
                retVal = "Feb ";
                break;

            case "03":
                retVal = "Mar ";
                break;

            case "04":
                retVal = "Apr ";
                break;

            case "05":
                retVal = "May ";
                break;

            case "06":
                retVal = "Jun";
                break;

            case "07":
                retVal = "Jul ";
                break;

            case "08":
                retVal = "Aug ";
                break;

            case "09":
                retVal = "Sept ";
                break;

            case "10":
                retVal = "Oct ";
                break;

            case "11":
                retVal = "Nov ";
                break;

            case "12":
                retVal = "Dec ";
                break;
        }
        retVal += s.substring(0,4);

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
