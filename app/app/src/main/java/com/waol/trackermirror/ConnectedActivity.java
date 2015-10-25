package com.waol.trackermirror;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.waol.trackermirror.utils.EstimotesHelper;
import com.waol.trackermirror.utils.Settings;
import com.waol.trackermirror.utils.TcpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

public class ConnectedActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String remoteIpAddress = "192.168.0.196";
    private static final Integer port = 8004;

    private BeaconManager beaconManager;
    private TcpClient tcpClient;

    private TextView beaconId;
    private TextView beaconDistance;
    private TextView connectionStatus;
    private Button userInformationButton;
    private LineColorPicker colorPicker;

    private boolean hadConnection = false;
    private int numberOfPackagesSent = 0;
    private boolean hasRunnedAnimations = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        this.beaconId = (TextView)findViewById(R.id.connected_beacon_id_txt);
        this.beaconDistance = (TextView)findViewById(R.id.connected_beacon_distance_txt);
        this.userInformationButton = (Button)findViewById(R.id.connected_userinformation_btn);
        this.connectionStatus = (TextView)findViewById(R.id.connected_connectionStatus);
        this.colorPicker = (LineColorPicker)findViewById(R.id.connected_colorPicker);

        // Create TCP client
        this.tcpClient = new TcpClient();
        this.connect();

        this.tcpClient.setConnectionListener(new TcpClient.OnSocketConnect() {
            @Override
            public void successfulConnection() {
                Log.d("ConnectionStatus", "Successful connection");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectionStatus.setText("Connection status:\n" + remoteIpAddress + ":" + port);
                    }
                });

                hadConnection = true;
            }
        });

        // Beacon range listening
        this.beaconManager = new BeaconManager(getApplicationContext());
        this.beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon closestBeacon = list.get(0);
                    ConnectedActivity.this.beaconId.setText(closestBeacon.getProximityUUID().toString());
                    ConnectedActivity.this.beaconDistance.setText("Distance:" + new DecimalFormat("#.##").format(Utils.computeAccuracy(closestBeacon)));

                    // Send data to server
                    if (tcpClient.isConnected()) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("info", createInfoJsonObject());
                            json.put("distance", Utils.computeAccuracy(closestBeacon) + "");

                            JSONObject colorJson = new JSONObject();
                            colorJson.put("r", Color.red(colorPicker.getColor()));
                            colorJson.put("g", Color.green(colorPicker.getColor()));
                            colorJson.put("b", Color.blue(colorPicker.getColor()));

                            json.put("color", colorJson);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        boolean sent = tcpClient.sendData(json.toString());

                        if(sent){
                            numberOfPackagesSent++;
                            connectionStatus.setText("Connection status:\n" + remoteIpAddress + ":" + port + "\n" + numberOfPackagesSent + " Packages sent");
                        }else {
                            // Fail to send package
                            connectionStatus.setText("Connection status:\n" + remoteIpAddress + ":" + port + "\nFail to send package");
                            tcpClient.closeSocket();

                            // Something went wrong create new connection
                            tcpClient = new TcpClient();
                            connect();
                        }

                    } else if (hadConnection) {
                        connectionStatus.setText("Connection status:\nLost connection");
                        hadConnection = false;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connect();
                            }
                        }, 2000);
                    }
                }
            }
        });

        this.userInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConnectedActivity.this, InformationActivity.class));
            }
        });

        findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(this);
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
        this.tcpClient.closeSocket();
    }


    private void connect() {
        // Check if connected to wifi
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectionStatus.setText("Connection status:\nLooking for wifi");
                    }
                });

                try{
                    // If no wifi, check again after 1 sec
                    while (!hasWifi()) {
                        Thread.sleep(1000);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setText("Connection status:\nHas wifi connection");
                        }
                    });

                    // When wifi connection execute TCP client connection
                    tcpClient.execute(remoteIpAddress, port + "");
                }
            }
        }).start();
    }

    private boolean hasWifi(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    private JSONObject createInfoJsonObject() throws JSONException {
        JSONObject infoJson = new JSONObject();
        infoJson.put("name", Settings.get(this, getString(R.string.saved_name)));
        infoJson.put("surname", Settings.get(this, getString(R.string.saved_surname)));
        infoJson.put("email", Settings.get(this, getString(R.string.saved_email)));
        infoJson.put("height", Integer.parseInt(Settings.get(this, getString(R.string.saved_height), "1")));
        infoJson.put("shoeSize", Integer.parseInt(Settings.get(this, getString(R.string.saved_shoesize), "1")));

        return infoJson;
    }

    @Override
    public void onGlobalLayout() {
        if(!hasRunnedAnimations){
            hasRunnedAnimations = true;
            findViewById(R.id.connected_logo).startAnimation(AnimationUtils.loadAnimation(this, R.anim.logo_scale_animation));

            ViewGroup wrapper = (ViewGroup)findViewById(R.id.connected_wrapper);
            int startOffset = 1500;
            int offset = 50;
            for (int i = 0; i < wrapper.getChildCount(); i++){
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.swipe_in_animation);
                animation.setStartOffset(startOffset);
                startOffset += offset;

                wrapper.getChildAt(i).startAnimation(animation);
            }
        }
    }
}