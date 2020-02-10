package com.example.scanme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import org.xmlpull.v1.XmlPullParser;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScanAndShareBarcodeScan extends Fragment implements ZXingScannerView.ResultHandler {
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
        MenuItem item = menu.findItem(R.id.scan);
        item.setVisible(false);

        MenuItem edit = menu.findItem(R.id.edit);
        edit.setVisible(false);

        MenuItem home = menu.findItem(R.id.home);
        home.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit:
                return true;
            case R.id.share:
                getActivity(). getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ScanAndShare())
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
    public void handleResult(final Result result) {
        final String myResult = result.getText();
        final String[] scanRes=myResult.split(" ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Scan Result");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview((ZXingScannerView.ResultHandler) getContext());
                getActivity(). getSupportFragmentManager().popBackStack();

            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                change(result.getText());
               // Log.d("demo result afte scab",result.getText());
            }
        });
        builder.setMessage(result.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    public void change(String result){
        getActivity(). getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ScanAndShare(result))
                .commit();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
