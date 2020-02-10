package com.example.scanme;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.WIFI_SERVICE;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.os.Looper.getMainLooper;
import static com.example.scanme.Share.MESSAGE_READ;
import static com.facebook.FacebookSdk.getApplicationContext;


public class ScanAndShare extends Fragment  implements  ZXingScannerView.ResultHandler {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    ImageView scanandshare;
    WifiManager wifiManager;
    Button wifi, discoverpeers,sendMsg;
    EditText textMsg;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    ZXingScannerView scannerView;
    List<WifiP2pDevice> peers = new ArrayList<>();
    String[] deviceName;
    WifiP2pDevice[] deviceArray;
    WifiP2pDevice deviceShare;
    WifiP2pConfig config = new WifiP2pConfig();
    String receiver_address = "";
    private WifiP2pInfo wifiP2pInfo;

    String ima = "";
    ServerClass serverClass;
    ClientClass clientClass;
    Socket ServerSocket, ClientSocket;
    sendReceiveThread sendReceiveThread;
    boolean deviceFound=false;
    String selectedImage = "";
    ArrayList<PojoMsg>ItemList=new ArrayList<PojoMsg>();
    ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    String sound="OFF";
    MediaPlayer mySong;
    ListView listview;
    ArrayList<String> getReceiver_address_List=new ArrayList<>();
    public ScanAndShare() {
    }

    public ScanAndShare(String text) {
        receiver_address = text;
        Log.d("demoreceiver_address", receiver_address);
    }

    private void connectingPeer() {
        Toast.makeText(getApplicationContext(), "Finding Near By devices", Toast.LENGTH_SHORT).show();

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reasonCode) {
               // Toast.makeText(getApplicationContext(), "discovery failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (receiver_address.length() == 0 && !deviceFound) {
                getReceiver_address_List.clear();
                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    getReceiver_address_List.add(device.deviceName+"::"+device.deviceAddress);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, getReceiver_address_List);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String address=getReceiver_address_List.get(position);
                        String mat[]=address.split("::");
                        Log.d("demo",mat[1]);
                        receiver_address=mat[1];
                        config.deviceAddress=receiver_address;
                        connecting();
                    }
                });
            } else {
                Log.d("receiver_address", receiver_address.trim());

                for (WifiP2pDevice device : peerList.getDeviceList()) {
                     Log.d("receiver_address---", device.deviceAddress);
                    if (device.deviceAddress.contentEquals(receiver_address.trim())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            Log.d("wifi device found::", peerList.get(receiver_address).toString());
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            deviceShare = peerList.get(receiver_address);
                        }
                        config.deviceAddress = deviceShare.deviceAddress;
                        //Toast.makeText(getContext(), "scan found", Toast.LENGTH_SHORT).show();
                        receiver_address = "";
                        deviceFound=true;
                        progressBar.setVisibility(View.VISIBLE);
                        connecting();
                    }
                }


            }
        }
    };


    public void connecting() {
        progressBar.setVisibility(View.VISIBLE);
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
               /// Toast.makeText(getContext(), "connecting..." + deviceShare.deviceName, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            InetAddress groupOwnerAddress = info.groupOwnerAddress;
            wifiP2pInfo = info;
            if (info.groupFormed && info.isGroupOwner) {
                getReceiver_address_List.clear();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, getReceiver_address_List);
                listview.setAdapter(adapter);
                progressBar.setVisibility(View.VISIBLE);
               // Toast.makeText(getApplicationContext(), "Your ownwer", Toast.LENGTH_SHORT).show();
                serverClass = new ServerClass();
                scanandshare.setVisibility(View.GONE);
                serverClass.start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                           // Toast.makeText(getApplicationContext(), "Discovery stopped", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(int reason) {

                        }
                    });
                }


                Log.d("onConnectionowner===", "onConnectionInfoAvailable");
            } else if (info.groupFormed) {
                getReceiver_address_List.clear();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, getReceiver_address_List);
                listview.setAdapter(adapter);
                progressBar.setVisibility(View.VISIBLE);
               // Toast.makeText(getApplicationContext(), "Your reseiver", Toast.LENGTH_SHORT).show();
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
                scanandshare.setVisibility(View.GONE);
                manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        //Toast.makeText(getContext(), "discovery stoped", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int reason) {

                    }
                });
                Log.d("onConnectionclient===", "onConnectionInfoAvailable");

            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("ScanMe & Share");
        setHasOptionsMenu(true);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }


        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        manager = (WifiP2pManager) getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(getContext(), getMainLooper(), null);
        receiver = new WifiBroadCastReceiver(manager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        View view = inflater.inflate(R.layout.fragment_scan_and_share, container, false);
        listview=view.findViewById(R.id.listview);
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView = view.findViewById(R.id.msgRecycle);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        scanandshare = view.findViewById(R.id.scanandshare);
        discoverpeers = view.findViewById(R.id.discoverpeers);
        wifi = view.findViewById(R.id.wifi);
        sendMsg = view.findViewById(R.id.sendMsg);
        textMsg = view.findViewById(R.id.textmsg);
        sendMsg.setEnabled(false);
        disconnect();
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PojoMsg pojoMsg=new PojoMsg();

                String msg=textMsg.getText().toString();
                textMsg.setText("");
                pojoMsg.msg=msg;
                pojoMsg.smsg="";
                ItemList.add(pojoMsg);
                mAdapter = new MsgAdapter(ItemList);
                recyclerView.setAdapter(mAdapter);
                sendReceiveThread.write(msg.getBytes());
            }
        });
        connectingPeer();

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sound.contains("OFF")) {
//                    mySong.start();
//                    mySong=MediaPlayer.create(getApplicationContext(), R.raw.);
//                    mySong=MediaPlayer.create(getApplicationContext(),R.raw.Siren);
                    //plst music = by sending string "SOUND"
                    String s="SOUND";
                    sendReceiveThread.write(s.getBytes());

                }else{
                    sound="OFF";
                    wifi.setText("MAKE SOUND");
                    mySong.release();
                    //mySong.release();
                    //stop playing
                }
                //showFileChooser();

            }
        });

        discoverpeers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                disconnect();
            }
        });
        return view;


    }

    public void disconnect(){
        discoverpeers.setText("Find NearBy Devices");
        ItemList.clear();
        deviceFound=false;
        wifi.setEnabled(false);
        mAdapter = new MsgAdapter(ItemList);
        recyclerView.setAdapter(mAdapter);
        scanandshare.setVisibility(View.VISIBLE);
        sendMsg.setEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);

        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                       // receiver_address = "";

                    }

                    @Override
                    public void onFailure(int reason) {

                    }
                });

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        connectingPeer();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
            // Uri  selectedImage = data.getData();
           // File file = new File(data.getData().getPath());
            selectedImage = data.getData().getPath();

            Log.d("demo", (data.getData().toString()));
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(receiver, intentFilter);

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
        // Log.e(TAG, "onSelfDeviceAvailable");
        Log.d("device name of host", "DeviceName: " + wifiP2pDevice.toString());
        // Log.e(TAG, "DeviceAddress: " + wifiP2pDevice.deviceAddress);
        // Log.e(TAG, "Status: " + wifiP2pDevice.status);
        String link = wifiP2pDevice.deviceAddress;
        link.replace(":", " ");
        //  Toast.makeText(getApplicationContext(), link, Toast.LENGTH_SHORT).show();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(link, BarcodeFormat.QR_CODE, 700, 700);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            scanandshare.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void wifiP2pEnabled(boolean b) {

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
//        MenuItem item = menu.findItem(R.id.home);
//        item.setVisible(false);
        if((wifi.isEnabled())){
            MenuItem item = menu.findItem(R.id.home);
            item.setVisible(false);
        }
        MenuItem edit = menu.findItem(R.id.edit);
        edit.setVisible(false);
        MenuItem share = menu.findItem(R.id.share);
        share.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.scan:
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("li")
                        .replace(R.id.container, new ScanAndShareBarcodeScan())
                        .commit();
                return true;
            case R.id.home:
                getActivity(). getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result result) {

    }


    public class ServerClass extends Thread {
        Socket ServerSocket;


        @Override
        public void run() {
            try {
                ima = "server";
               ServerSocket serverSocket = new ServerSocket(9022);
                ServerSocket = serverSocket.accept();
                sendReceiveThread = new sendReceiveThread(ServerSocket);
                //sendToast();
                Log.d("setting cocket", "   setting");
               changeButton();

                sendReceiveThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendToast() {
    }

    private void changeButton() {
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                progressBar.setVisibility(View.INVISIBLE);
                sendMsg.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Your Connected", Toast.LENGTH_SHORT).show();
                wifi.setEnabled(true);
                //        MenuItem item = menu.findItem(R.id.home);
//        item.setVisible(false);
                discoverpeers.setText("Disconnect");

            }
        });

    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    byte readBuff[]  = (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);

                    if(tempMsg.contains("SOUND")){
                        ///plsy sound
                        if(sound.contentEquals("OFF")) {
                            mySong = MediaPlayer.create(getApplicationContext(), R.raw.siren);
                            mySong.setLooping(true);
                            mySong.start();
                            sound = "ON";
                        }
                        wifi.setText("STOP SOUND");

                    }else {
                        //textMsg.setText(tempMsg);
                        PojoMsg pojoMsg = new PojoMsg();
                        pojoMsg.msg = "";
                        pojoMsg.smsg = tempMsg;
                        ItemList.add(pojoMsg);
                        mAdapter = new MsgAdapter(ItemList);
                        recyclerView.setAdapter(mAdapter);
                        Log.d("msg====", tempMsg);
                    }
                    break;
            }
            return true;
        }
    });
    public class sendReceiveThread extends Thread {
        Socket socketMain;
        InputStream inputStream;
        OutputStream outputStream;

        public sendReceiveThread(Socket skt) {
            socketMain = skt;
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
                         // changeText();
//                        byte[] buf = new byte[1024];
//                        int len;
//                        while((len=inputStream.read(buf))>0){
//                            f.write(buf,0,len);
//                        }

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
                outputStream.write(bytes);
//                InputStream inputStreama = cr.openInputStream(selectedImage);
//                while ((len = inputStreama.read(buf)) != -1) {
//                    outputStream.write(buf, 0, len);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeText() {
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                textMsg.setText("");

            }
        });

    }

    public class ClientClass extends Thread {
            //Socket ClientSocket;
            String hostAdd;

            public ClientClass(InetAddress hostAddress) {
                hostAdd = hostAddress.getHostAddress();
                ClientSocket = new Socket();

            }

            @Override
            public void run() {
                try {
                    ima = "client";
                    changeButton();
                    ClientSocket.connect(new InetSocketAddress(hostAdd, 9022), 500);
                    sendReceiveThread = new sendReceiveThread(ClientSocket);
                    sendReceiveThread.start();
                    //sendToast();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
