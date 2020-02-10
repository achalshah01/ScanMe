package com.example.scanme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static androidx.core.content.ContextCompat.getSystemService;


public class ScanBarcode extends Fragment implements ZXingScannerView.ResultHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    ZXingScannerView scannerView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    String name,email,phone,token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        name=preferences.getString("name","");
        email=preferences.getString("email","");
        phone=preferences.getString("phone","");
        token= preferences.getString("token", "");
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        scannerView=new ZXingScannerView(getActivity().getApplicationContext());
        //return inflater.inflate((XmlPullParser) scannerView, container, false);
        if(checkPermission())
        {
            Toast.makeText(getActivity(), "Permission already granted!", Toast.LENGTH_LONG).show();
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
        else
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        return scannerView;
    }
    public boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        MenuItem edit = menu.findItem(R.id.edit);
        edit.setVisible(false);
        MenuItem share = menu.findItem(R.id.share);
        share.setVisible(false);
        MenuItem item = menu.findItem(R.id.scan);
        item.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit:
                return true;
            case R.id.share:
                return true;
            case R.id.home:
                getActivity(). getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        final String[] scanRes=myResult.split(" ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Contact");
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview((ZXingScannerView.ResultHandler) getContext());
                String tokenId=scanRes[4];
                add(tokenId);
                getActivity(). getSupportFragmentManager().popBackStack();

            }
        });
        builder.setNeutralButton("Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tokenId=scanRes[4];


                Log.d("demo",scanRes[4].toString());
                Log.d("demo",token);
                if(isNetworkAvailable()){
                    callingApi(tokenId);
                }
                else{
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String qId=preferences.getString("qId","");
                    if(qId.length()==0){
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("qId",tokenId);
                        editor.apply();
                    }else{
                        qId=qId+" "+tokenId;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("qId",qId);
                        editor.apply();
                    }


                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, scanRes[3])
                            .putExtra(ContactsContract.Intents.Insert.PHONE, scanRes[2])
                            .putExtra(ContactsContract.Intents.Insert.NAME,scanRes[0]+" "+scanRes[1]);
                    startActivity(intent);
                }


            }

            public boolean isNetworkAvailable() {
                ConnectivityManager connectivityManager = ((ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE));
                return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
            }

            private void callingApi(String tokenId) {
                StringBuilder sb = new StringBuilder();
        sb.append("https://finalscanme12.herokuapp.com/api/contact/add");
        Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        final OkHttpClient client = new OkHttpClient();

        JSONObject postdata=new JSONObject();
        try {
            postdata.put ("userId",token);
            postdata.put ("contactId",tokenId);
                   } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body =RequestBody.create(JSON,postdata.toString());
        Request request = new Request.Builder()
                .url(sb.toString())
                .header("Content-Type", "application/json")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // Log.d("DEMO",response.body().string());
                String responseData=response.body().string();

                Log.d("DEMO123=",responseData);

                // onComplete(Token);
                client.dispatcher().executorService().shutdown();
                Handler mainHandler = new Handler(Looper.getMainLooper());

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                       // mListener.changeToHomeFragment(Token);
                        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
// Sets the MIME type to match the Contacts Provider
                        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, scanRes[3])
                                .putExtra(ContactsContract.Intents.Insert.PHONE, scanRes[2])
                                .putExtra(ContactsContract.Intents.Insert.NAME,scanRes[0]+" "+scanRes[1]);

                        startActivity(intent);


                    }
                });


            }

        });
            }
        });
        builder.setMessage("Do you want to add "+scanRes[0]+" "+scanRes[1]+" ?" );
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    private void add(String tokenId) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://finalscanme12.herokuapp.com/api/contact/add");
        Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        final OkHttpClient client = new OkHttpClient();

        JSONObject postdata=new JSONObject();
        try {
            postdata.put ("userId",token);
            postdata.put ("contactId",tokenId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body =RequestBody.create(JSON,postdata.toString());
        Request request = new Request.Builder()
                .url(sb.toString())
                .header("Content-Type", "application/json")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // Log.d("DEMO",response.body().string());
                String responseData=response.body().string();

                Log.d("DEMO123=",responseData);

                // onComplete(Token);
                client.dispatcher().executorService().shutdown();
                Handler mainHandler = new Handler(Looper.getMainLooper());

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // mListener.changeToHomeFragment(Token);
                    }
                });


            }

        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
