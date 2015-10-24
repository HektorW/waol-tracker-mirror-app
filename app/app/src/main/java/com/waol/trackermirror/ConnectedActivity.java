package com.waol.trackermirror;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.waol.trackermirror.utils.EstimotesHelper;
import com.waol.trackermirror.utils.TcpClient;

import java.text.DecimalFormat;
import java.util.List;

public class ConnectedActivity extends AppCompatActivity {

    private static final String remoteIpAddress = "192.168.0.196";
    private static final Integer port = 8004;

    private BeaconManager beaconManager;
    private TcpClient tcpClient;

    private TextView beaconId;
    private TextView beaconDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        this.beaconId = (TextView)findViewById(R.id.connected_beacon_id_txt);
        this.beaconDistance = (TextView)findViewById(R.id.connected_beacon_distance_txt);

        // Create connection with tcp server
        new ConnectTask().execute();

        this.beaconManager = new BeaconManager(getApplicationContext());
        this.beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if(!list.isEmpty()) {
                    Beacon closestBeacon = list.get(0);
                    ConnectedActivity.this.beaconId.setText(closestBeacon.getProximityUUID().toString());
                    ConnectedActivity.this.beaconDistance.setText(new DecimalFormat("#.##").format(Utils.computeAccuracy(closestBeacon)));

                    try{
                        tcpClient.sendData(Utils.computeAccuracy(closestBeacon) + "");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(EstimotesHelper.DefaultRegion());
            }
        });
    }

    @Override
    protected void onPause() {
        this.beaconManager.stopRanging(EstimotesHelper.DefaultRegion());
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.beaconManager.disconnect();
    }

    private class ConnectTask extends AsyncTask<String, String, TcpClient>{

        @Override
        protected TcpClient doInBackground(String... params) {

            tcpClient = new TcpClient(remoteIpAddress, port, new TcpClient.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {

                }
            });
            tcpClient.run();

            return null;
        }
    }
}
