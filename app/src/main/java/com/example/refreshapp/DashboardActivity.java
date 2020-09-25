 package com.example.refreshapp;

 import androidx.appcompat.app.AppCompatActivity;

 import android.app.AlertDialog;
 import android.content.Context;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.net.ConnectivityManager;
 import android.os.Bundle;
 import android.view.Menu;
 import android.view.MenuInflater;
 import android.view.MenuItem;
 import android.view.View;
 import android.view.Window;
 import android.widget.Button;
 import android.widget.CompoundButton;
 import android.widget.LinearLayout;
 import android.widget.Switch;
 import android.widget.Toast;

 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.database.FirebaseDatabase;


 public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout linearLayoutTemp;
    private LinearLayout linearLayoutEnergyCost;
    private LinearLayout linearLayoutBill;
    private Switch switchPowerStatus;

    //  Controller
//    public UserC userc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        setTitle("Dashboard");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            // user not logged in, return to login screen
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        final UserC userc = (UserC) getApplicationContext();
        userc.initialize();
        userc.setDashboardIds(this);
        userc.setDashboardListeners(this);
        userc.setSwitchListener( this );
        findViews();
        checkNetworkConnection();
    }

     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.menu_actions, menu);
         return true;
     }

    private void findViews() {
        linearLayoutTemp = findViewById( R.id.linearLayoutTemp );
        linearLayoutEnergyCost = findViewById( R.id.linearLayoutEnergyCost );
        linearLayoutBill = findViewById( R.id.linearLayoutBill );
        switchPowerStatus = findViewById( R.id.switchPowerStatus );

        linearLayoutTemp.setOnClickListener( this );
        linearLayoutEnergyCost.setOnClickListener( this );
        linearLayoutBill.setOnClickListener( this );
        switchPowerStatus.setOnClickListener( this );
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if ( v == linearLayoutTemp){
            startActivity(new Intent(this, Temperature.class));
        }
        else if ( v == linearLayoutEnergyCost){
            startActivity(new Intent(this, Power.class));
        }
        else if ( v == linearLayoutBill){
            startActivity(new Intent(this, Bill.class));
        }
        else if ( v == switchPowerStatus){
            Boolean status = switchPowerStatus.isChecked();
            Toast.makeText(DashboardActivity.this, "Turning fridge " + (status ? "on" : "off"), Toast.LENGTH_LONG).show();
            final UserC u = (UserC)getApplicationContext();
            u.setPowerStatus(status);
        }
    }

     public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.actionRefresh:
                final UserC u = (UserC)getApplicationContext();
                u.setDashboardListeners(this);
                Toast.makeText(DashboardActivity.this, "Updating", Toast.LENGTH_LONG).show();
                return true;

            case R.id.actionEditProfile:
                startActivity(new Intent(this, EditProfile.class));
                return true;

            case R.id.actionLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                return true;

            default:
                return false;
        }
     }

     public void checkNetworkConnection(){
         ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         if(cm.getActiveNetwork() == null) {
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setTitle("No internet Connection");
             builder.setMessage("Please turn on internet connection to continue");
             builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                 }
             });
             AlertDialog alertDialog = builder.create();
             alertDialog.show();
         }
     }
}
