package com.example.scanme;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.WIFI_SERVICE;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.os.Looper.getMainLooper;
import static com.facebook.FacebookSdk.getApplicationContext;


public class Share extends Fragment {
    private OnFragmentInteractionListener mListener;
 Button share,discover,connect,send,close,share1;
 TextView textView;
 EditText editText;
     WifiManager wifiManager;
    WifiConfiguration currentConfig;
    WifiManager.LocalOnlyHotspotReservation hotspotReservation;
    private WifiManager.LocalOnlyHotspotReservation mReservation;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    WifiP2pManager.PeerListListener myPeerListListener;
List <WifiP2pDevice> peers=new ArrayList<>();
String [] deviceName;
WifiP2pDevice[] deviceArray;
    WifiP2pDevice deviceShare;
    WifiP2pConfig config = new WifiP2pConfig();
ImageView imageView;
    ServerClass serverClass;
    ClientClass clientClass;
    sendReceiveThread sendReceiveThread;

String ima="";
    Uri selectedImage;
    ServerSocket serverSocket;
    Socket ServerSocket,ClientSocket;
    static final int MESSAGE_READ=1;
    File file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/"
            + "/wifip2pshared-1"
            + ".jpg");
     FileOutputStream  f;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        try {
             f = new FileOutputStream (file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        View view= inflater.inflate(R.layout.fragment_share, container, false);
        share=view.findViewById(R.id.share);
        discover=view.findViewById(R.id.discover);
        connect=view.findViewById(R.id.connect);
        send=view.findViewById(R.id.send);
        close=view.findViewById(R.id.close);
        textView=view.findViewById(R.id.textView);
        editText=view.findViewById(R.id.editText);
        share1=view.findViewById(R.id.share1);
        imageView=view.findViewById(R.id.imageView);

        wifiManager= (WifiManager) getApplicationContext(). getSystemService(WIFI_SERVICE);
        manager = (WifiP2pManager) getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(getContext(), getMainLooper(), null);
        //receiver = new WifiBroadCastReceiver(manager, channel, this);
        intentFilter=new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


share1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        showFileChooser();
    }
});
        Log.d("demo====",Build.DEVICE+" "+Build.ID);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exqListener();

            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fine();
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connecting();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=editText.getText().toString();
                sendReceiveThread.write(msg.getBytes());

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                  //  serverSocket.close();
                    if(ima.contentEquals("server")){
                        serverSocket.close();
                       // ServerSocket.close();
                        Log.d("serverClient===", "close close");
                    }else  if(ima.contentEquals("client")){
                        ClientSocket.close();

                        Log.d("closeClient===", "close close");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
public void connecting(){
      manager.connect(channel,config, new WifiP2pManager.ActionListener() {
          @Override
          public void onSuccess() {
              Toast.makeText(getContext(), "connecting..."+deviceShare.deviceName, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onFailure(int reason) {

          }
      });
}

WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        InetAddress groupOwnerAddress=info.groupOwnerAddress;
        if(info.groupFormed && info.isGroupOwner){
            Toast.makeText(getApplicationContext(), "Your ownwer", Toast.LENGTH_SHORT).show();
            serverClass=new ServerClass();

            serverClass.start();
            Log.d("onConnectionowner===","onConnectionInfoAvailable");
        }else if(info.groupFormed){
            Toast.makeText(getApplicationContext(), "Your reseiver", Toast.LENGTH_SHORT).show();
             clientClass=new ClientClass(groupOwnerAddress);
             clientClass.start();
            Log.d("onConnectionclient===","onConnectionInfoAvailable");

        }
    }
};



    public void fine(){
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "discovery started", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(getApplicationContext(), "discovery failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            if(!peerList.getDeviceList().equals(peers)){
                 peers.clear();
                 peers.addAll(peerList.getDeviceList());

                  deviceName=new String[peerList.getDeviceList().size()];
                deviceArray=new WifiP2pDevice[peerList.getDeviceList().size()];

                int index=0;

                for(WifiP2pDevice device: peerList.getDeviceList()){
                    deviceName[index]=device.deviceName;
                    deviceArray[index]=device;index++;
                    if(device.deviceAddress.contentEquals("aa:51:5b:bd:a7:41")){
                        Log.d("wifi device found::", peerList.get("aa:51:5b:bd:a7:41").toString());
                        deviceShare = peerList.get("aa:51:5b:bd:a7:41");
                        config.deviceAddress = deviceShare.deviceAddress;
                    }
                }


            }


        }
    };

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    byte readBuff[]  = (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    textView.setText(tempMsg);
                    Log.d("msg====",tempMsg);

                    break;
            }
            return true;
        }
    });

    @Override
    public void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(receiver, intentFilter);

    }

    public void exqListener(){
        if(wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            share.setText("off");
        }else{
            wifiManager.setWifiEnabled(true);
            share.setText("on");
        }

    }
public void startHotSpot(){
    WifiManager wifimanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        wifimanager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback()
        {
            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                Log.d("decode","syarted");
            }
        },new Handler());
    }
}




    public String wifiName(){
        WifiManager wifimanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        Method[] methods = wifimanager.getClass().getDeclaredMethods();
        for (Method m: methods) {
            if (m.getName().equals("getWifiApConfiguration")) {
                try {
                    WifiConfiguration config = (WifiConfiguration)m.invoke(wifimanager);
                    Log.d("demo123Q",config.toString()) ;
                    Log.d("demo123Q23",config.BSSID) ;

                    return config.SSID;

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                // here, the "config" variable holds the info, your SSID is in
                // config.SSID
            }
        }
        return null;

    }

    private void showFileChooser() {

        try {
            // Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // startActivityForResult(i, RESULT_LOAD_IMAGE);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 0);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }

    }


    public ArrayList<String> getClientList() {
        ArrayList<String> clientList = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] clientInfo = line.split(" +");
                String mac = clientInfo[3];
                Log.d("demoWifi",line);
                //Log.d("demoWifi1",clientInfo[4]+" "+clientInfo[5]);

                if (mac.matches("..:..:..:..:..:..")) { // To make sure its not the title
                    clientList.add(clientInfo[0]);
                }
            }
        } catch (java.io.IOException aE) {
            aE.printStackTrace();
            return null;
        }
        return clientList;
    }
//
//    @SuppressLint("NewApi")
//    private void turnOffHotspot() {
//        if (mReservation != null) {
//            mReservation.close();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
             selectedImage = data.getData();
            try {
                InputStream inputStreamas=getApplicationContext().getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap=BitmapFactory.decodeStream(inputStreamas);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            Log.d("demo",(data.getData().toString()));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
        Log.d("onSelfDeviceAvailable", "onSelfDeviceAvailable");
        Log.d("onSelfDeviceAvailable", "DeviceName: " + wifiP2pDevice.deviceName);
        Log.d("onSelfDeviceAvailable", "DeviceAddress: " + wifiP2pDevice.deviceAddress);
        Log.d("onSelfDeviceAvailable", "Status: " + wifiP2pDevice.status);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public  class ServerClass extends Thread{
        Socket ServerSocket;


       @Override
        public void run(){
           try {
               ima="server";
         serverSocket=new ServerSocket(9019);
               ServerSocket=serverSocket.accept();
               sendReceiveThread=new sendReceiveThread(ServerSocket);

               Log.d("setting cocket","   setting");
               sendReceiveThread.start();

           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    public class sendReceiveThread extends  Thread{
        Socket socketMain;
        InputStream inputStream;
        OutputStream outputStream;
        public sendReceiveThread(Socket skt){
            socketMain=skt;
            try {
                inputStream=socketMain.getInputStream();
                outputStream=socketMain.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        @Override
        public void run(){
            byte buffer[]  = new byte[1024];
            int bytes;

          while(socketMain!=null && !socketMain.isClosed()){
              try {
                  bytes=inputStream.read(buffer);
                  if(bytes>0){
                      handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();



                      byte[] buf = new byte[1024];
                      int len;
                      while((len=inputStream.read(buf))>0){
                          f.write(buf,0,len);
                      }

//                      Bitmap b = BitmapFactory.decodeStream(inputStream);
//                        imageView.setImageBitmap(b);
//                      b.compress(Bitmap.CompressFormat.PNG, 100, f);
                      //f.write(bytes);
                     // f.close();
                      //copyFile(inputStream, new FileOutputStream(f));
                  }
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        }


        public void write(byte[] bytes){
            try {
                int len;
                byte buf[]  = new byte[1024];
                ContentResolver cr = getApplicationContext().getContentResolver();
                //outputStream.write(bytes);
                InputStream inputStreama = cr.openInputStream(selectedImage);
                while ((len = inputStreama.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{
        //Socket ClientSocket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress){
            hostAdd=hostAddress.getHostAddress();
            ClientSocket=new Socket();

        }
        @Override
        public void run(){
            try {
                ima="client";
                ClientSocket.connect(new InetSocketAddress(hostAdd,9019),500);
               sendReceiveThread=new sendReceiveThread(ClientSocket);
               sendReceiveThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
