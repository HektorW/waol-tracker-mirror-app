package com.waol.trackermirror;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.beaconManager = new BeaconManager(getApplicationContext());
//
//        this.beaconManager.setRangingListener(new BeaconManager.RangingListener() {
//            @Override
//            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
//                if(!list.isEmpty()){
//                    Beacon beacon = list.get(0);
//                    double accuracy = Utils.computeAccuracy(beacon);
//                    ((TextView) findViewById(R.id.main_header_txt)).setText(beacon.getProximityUUID().toString() + "\n" + accuracy);
//                }
//            }
//        });

        this.beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                startActivity(new Intent(MainActivity.this, ConnectedActivity.class));
            }

            @Override
            public void onExitedRegion(Region region) {
            }
        });
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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
