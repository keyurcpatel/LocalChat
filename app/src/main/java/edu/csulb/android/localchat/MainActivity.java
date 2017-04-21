package edu.csulb.android.localchat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.ListIterator;

import edu.csulb.android.localchat.Utilities.WiFiDirectBroadcastReceiver;

import static edu.csulb.android.localchat.Utilities.WiFiDirectBroadcastReceiver.getDeviceList;
import static edu.csulb.android.localchat.Utilities.WiFiDirectBroadcastReceiver.getDeviceNames;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiP2pManager mManager;
    Channel mChannel;
    IntentFilter mIntentFilter;
    BroadcastReceiver mReceiver;

    TextView wifiListTextView;
    ListView wifiListView;

    BroadcastReceiver rcvWifiScan;

    final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiListTextView = (TextView) findViewById(R.id.wifiListTextView);
        wifiListView = (ListView) findViewById(R.id.wifiListView);

//        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//        if(wifiManager.isWifiEnabled()) {
//
//            createBroadcastReceiver();
//            registerReceiver(rcvWifiScan, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//
//            scanNetwork(null);
//
//            ListIterator<WifiConfiguration> configs = wifiManager.getConfiguredNetworks().listIterator();
//
//            String allConfigs = "Configs: \n";
//            while (configs.hasNext()) {
//                WifiConfiguration config = configs.next();
//                String configInfo = "Name: " + config.SSID +
//                        "; priority = " + config.priority;
//
//                Log.v(TAG + "WiFi", configInfo);
//
//                allConfigs += configInfo + "\n";
//            }
//            wifiListTextView.setText(allConfigs);
//        }

        mManager = (WifiP2pManager) getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "discoverpeers on success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "discoverpeers on failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void createBroadcastReceiver() {
        rcvWifiScan = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> wifiList = wifiManager.getScanResults();
                int foundCount = wifiList.size();
                Toast.makeText(context, "Scan done, " + foundCount + " found", Toast.LENGTH_SHORT).show();
                ListIterator<ScanResult> results = wifiList.listIterator();
                String fullInfo = "Scan Results: \n";
                while(results.hasNext()) {
                    ScanResult info = results.next();
                    String wifiInfo = "Name: " + info.SSID +
                            "; capabilities = " + info.capabilities +
                            "; sig str = " + info.level + "dBm";
                    Log.v(TAG + "Wifi", wifiInfo);

                    fullInfo += wifiInfo + "\n";
                }
                wifiListTextView.setText(fullInfo);
            }
        };
    }


    public void scanNetwork(View view) {
        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MainActivity.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            MainActivity.this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
        wifiManager.startScan();
    }

    public void connectToDevice(final WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "Successfully connected to device: "+device.deviceName);
            }

            @Override
            public void onFailure(int reason) {
                Log.v(TAG, "Failed connecting to device: "+device.deviceName);
            }
        });
    }

    public void refreshList(View view) {
        wifiListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getDeviceNames()));

    }
}
