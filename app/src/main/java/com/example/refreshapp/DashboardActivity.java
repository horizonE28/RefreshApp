 package com.example.refreshapp;

 import androidx.appcompat.app.AppCompatActivity;

 import android.content.Intent;
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
    public UserC userc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide();
        //setSupportActionBar(findViewById(R.id.));

        setContentView(R.layout.activity_dashboard);
        setTitle("Dashboard");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            // user not logged in, return to login screen
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        userc = new UserC();
        userc.setDashboardIds(this);
        userc.setDashboardListeners(this);
        findViews();
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
        userc.setSwitchListener( this );
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
            userc.setPowerStatus(status);
        }
    }

     public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.actionRefresh:
                userc.setDashboardListeners(this);
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
}
