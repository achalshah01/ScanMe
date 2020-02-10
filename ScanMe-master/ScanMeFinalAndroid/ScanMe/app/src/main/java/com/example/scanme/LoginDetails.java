package com.example.scanme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginDetails extends Fragment {
 CallbackManager callbackManager;
 Button loginButton,save,SignUp;
 EditText phone,name,email, password;
 ProgressBar progressBar5;
    private static final String EMAIL = "email";

    private OnFragmentInteractionListener mListener;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("ScanMe - LogIn");
        // Inflate the layout for this fragment
        callbackManager = CallbackManager.Factory.create();
        View view = inflater.inflate(R.layout.fragment_login_details, container, false);
        SignUp =  view.findViewById(R.id.SignUp);
        email =  view.findViewById(R.id.email);
        password=view.findViewById(R.id.PasswordLogin);
        save = view.findViewById(R.id.save);
        progressBar5 = view.findViewById(R.id.progressBar5);
        progressBar5.setVisibility(View.INVISIBLE);
       /// loginButton = (LoginButton) view.findViewById(R.id.login_button);




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String ee=null, ps=null;
                    if(email.getText().toString().length()==0){
                        Toast.makeText(getContext(), "Enter Email", Toast.LENGTH_SHORT).show();
                        ee="null";}
                if(password.getText().toString().length()==0){
                    Toast.makeText(getContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                    ps="null";}
                    else {
                        progressBar5.setVisibility(View.VISIBLE);

                        ee = email.getText().toString();
                        ps= password.getText().toString();
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        final OkHttpClient client = new OkHttpClient();
                        JSONObject postdata = new JSONObject();
                        try {

                            postdata.put("email", ee);
                            postdata.put("password", ps);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestBody body = RequestBody.create(JSON, postdata.toString());
                        Request request = new Request.Builder()
                                .url("https://finalscanme12.herokuapp.com/api/users/getOne")
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
                                String responseData = response.body().string();
                                try {
                                    JSONObject token = new JSONObject(responseData);
                                    if (token.has("msg")) {
                                        Handler mainHandler = new Handler(Looper.getMainLooper());

                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar5.setVisibility(View.INVISIBLE);

                                                Toast.makeText(getContext(), "Email Dont Exists", Toast.LENGTH_SHORT).show();


                                            }
                                        });

                                    } else {
                                      final JSONObject  TokenC = token.getJSONObject("user");
                                        Log.d("DEMO1233=", TokenC.toString());

                                        // onComplete(Token);
                                        client.dispatcher().executorService().shutdown();
                                        Handler mainHandler = new Handler(Looper.getMainLooper());

                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), "loged", Toast.LENGTH_SHORT).show();
                                                try {
                                                    progressBar5.setVisibility(View.INVISIBLE);
                                                    mListener.changeToHomeFragment(TokenC.get("_id").toString(), TokenC.get("firstName").toString()
                                                            , TokenC.get("lastName").toString(), TokenC.get("email").toString(), TokenC.get("phone").toString(),TokenC.get("city").toString());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                    }
                //  onCo

            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity(). getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detailContainer, new SignUp())
                        .commit();
            }
        });
        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event


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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void changeToHomeFragment(String token, String firstName, String lastName, String email, String phoneNumber,String city);

    }
}
