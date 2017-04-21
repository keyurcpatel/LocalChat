package edu.csulb.android.localchat.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import edu.csulb.android.localchat.MainActivity;

/**
 * Created by KEYUR on 19-04-2017.
 * Time: 13:05
 * Project: LocalChat
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private MainActivity mActivity;

    private static WifiP2pDeviceList deviceList;

    private static List<String> deviceNames;


    private static final String TAG = "WiFiDirectReceiver";

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

    }

    public static List<String> getDeviceNames() {
        return deviceNames;
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Toast.makeText(context, "Wifi P2P is enabled", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Wifi P2P is not enabled", Toast.LENGTH_SHORT).show();
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                if(mManager != null) {
                    mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            setDeviceList(peers);
                            deviceNames.clear();
                            for(WifiP2pDevice device: peers.getDeviceList())
                            {
                                WiFiDirectBroadcastReceiver.deviceNames.add(device.deviceName);
                            }

                        }
                    });
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                break;
            default:
                break;
        }

    }

    public static WifiP2pDeviceList getDeviceList() {
        return deviceList;
    }

    public static void setDeviceList(WifiP2pDeviceList deviceList) {
        deviceList = deviceList;
    }
}
