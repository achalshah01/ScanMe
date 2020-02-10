package com.example.scanme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.login.Login;

public class DetainsEntry extends AppCompatActivity implements  SignUp.OnFragmentInteractionListener, LoginDetails.OnFragmentInteractionListener {
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detains_entry);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detailContainer, new LoginDetails())
                .commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
     public void changeToHomeFragment(String token, String firstName, String lastName, String email, String phoneNumber,String city){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token",token);
        editor.putString("name",firstName+" "+ lastName);
        editor.putString("phone",phoneNumber);
        editor.putString("email",email);
        editor.putString("city",city);

        editor.apply();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }



}
