package com.waol.trackermirror;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.waol.trackermirror.utils.EstimotesHelper;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private TextView searchText;

    private boolean foundBeacon = false;
    private String dots = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.searchText = (TextView)findViewById(R.id.main_search_txt);
        findViewById(R.id.main_userinformation_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InformationActivity.class));
            }
        });

        this.beaconManager = new BeaconManager(getApplicationContext());
        this.beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                foundBeacon = true;
                startActivity(new Intent(MainActivity.this, ConnectedActivity.class));
            }

            @Override
            public void onExitedRegion(Region region) {
            }
        });

        updateSearchText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(EstimotesHelper.DefaultRegion());
            }
        });
    }

    @Override
    protected void onPause() {
        this.beaconManager.stopMonitoring(EstimotesHelper.DefaultRegion());
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.beaconManager.disconnect();
    }

    private void updateSearchText(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dots.length() > 2){
                    dots = "";
                }
                dots += ".";
                searchText.setText("Searching beacons" + dots);

                if (!foundBeacon)
                    updateSearchText();
            }
        }, 1000);
    }
}
