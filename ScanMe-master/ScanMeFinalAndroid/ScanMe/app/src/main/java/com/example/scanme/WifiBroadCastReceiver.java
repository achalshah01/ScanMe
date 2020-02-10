package com.example.scanme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiBroadCastReceiver extends BroadcastReceiver {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    ScanAndShare mActivity;
    WifiP2pManager.PeerListListener myPeerListListener;


    public WifiBroadCastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ScanAndShare share) {
        this.mManager = manager;
        this.mChannel =  channel;
        this.mActivity = share;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-/Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -100);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mActivity.wifiP2pEnabled(true);
            } else {
                mActivity.wifiP2pEnabled(false);

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (mManager != null) {
                mManager.requestPeers((WifiP2pManager.Channel) mChannel, mActivity.peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections

            if(mManager==null)return;
            NetworkInfo networkInfo=intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()){
                mManager.requestConnectionInfo(mChannel,mActivity.connectionInfoListener);
            }else{
                mActivity.disconnect();
               // Toast.makeText(context, "device disconnected or not connected", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
           // Toast.makeText(context, "device ganged", Toast.LENGTH_SHORT).show();
            mActivity.onSelfDeviceAvailable(wifiP2pDevice);
        }
    }

}
