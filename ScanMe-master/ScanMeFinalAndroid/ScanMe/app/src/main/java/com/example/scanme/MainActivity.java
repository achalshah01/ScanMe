package com.example.scanme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity implements BarCode.OnFragmentInteractionListener,ScanBarcode.OnFragmentInteractionListener
,Share.OnFragmentInteractionListener,ScanAndShare.OnFragmentInteractionListener,ScanAndShareBarcodeScan.OnFragmentInteractionListener,EditDetails.OnFragmentInteractionListener,AddedFragments.OnFragmentInteractionListener{
    SharedPreferences sharedpreferences;
   String name,email,phone,token,city,qId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
         name=preferences.getString("name","");
        email=preferences.getString("email","");
        phone=preferences.getString("phone","");
        token= preferences.getString("token", "");
        city= preferences.getString("city", "");
        qId= preferences.getString("qId", "");



        if(phone.length()==0){
            Intent intent=new Intent(getApplicationContext(),DetainsEntry.class);
            startActivity(intent);
        }else{

        getSupportFragmentManager().beginTransaction().addToBackStack("list")
                .replace(R.id.container, new BarCode(name,email,phone, token,city,qId))
                .commit();
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void logout() {
        finish();
        startActivity(getIntent());
    }
}
