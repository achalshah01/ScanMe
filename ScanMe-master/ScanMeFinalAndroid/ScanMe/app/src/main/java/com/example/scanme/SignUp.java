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
import android.widget.RadioGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class SignUp extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText phoneNumber;
    EditText city;
    EditText password;
    EditText confirmPassword;
    RadioGroup genderRadioGroup;
    Button signUp;
    String FirstName, LastName, Email, PhoneNumber, Gender="Male", City,dateOfBirth, Password;
    String Token;
     ProgressBar progressBar3;

    private OnFragmentInteractionListener mListener;

    public SignUp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUp.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUp newInstance(String param1, String param2) {
        SignUp fragment = new SignUp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        getActivity().setTitle("ScanMe- Sign Up ");


        View view=inflater.inflate(R.layout.fragment_sign_up, container, false);
        firstName=view.findViewById(R.id.editFirstName);
        lastName=view.findViewById(R.id.email);
        email=view.findViewById(R.id.editEmailId);
        phoneNumber=view.findViewById(R.id.PhoneNumber);
        city=view.findViewById(R.id.cty);
        signUp=view.findViewById(R.id.button2);
        password=view.findViewById(R.id.Password);
        confirmPassword=view.findViewById(R.id.confirmPassword);
        genderRadioGroup = view.findViewById(R.id.radioGroup);
        progressBar3=view.findViewById(R.id.progressBar3);
        progressBar3.setVisibility(View.INVISIBLE);
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.maleRadioButton:
                        Gender = "Male";
                        break;
                    case R.id.femaleRadioButton:
                        Gender = "Female";
                        break;
                    default:
                        break;
                }
            }
        });

            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (firstName.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getContext(), "Enter First Name", Toast.LENGTH_SHORT).show();
                        firstName.setError("This field can not be blank");
                    } else if (lastName.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getContext(), "Enter Last Name", Toast.LENGTH_SHORT).show();
                        lastName.setError("This field can not be blank");
                    } else if (email.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getContext(), "Enter Email ID", Toast.LENGTH_SHORT).show();
                        email.setError("This field can not be blank");}
                        else if (phoneNumber.getText().toString().trim().equalsIgnoreCase("")) {
                            Toast.makeText(getContext(), "Enter phone number", Toast.LENGTH_SHORT).show();
                            phoneNumber.setError("This field can not be blank");

                    }
                    else if ((password.getText().toString().trim().equalsIgnoreCase(""))) {
                        Toast.makeText(getContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                        password.setError("This field can not be blank");
                    }
                    else if ((confirmPassword.getText().toString().trim().equalsIgnoreCase(""))) {
                        Toast.makeText(getContext(), "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                        confirmPassword.setError("This field can not be blank");
                    }else if (genderRadioGroup.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(getContext(), "Select Gender", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar3.setVisibility(View.VISIBLE);

                            FirstName = firstName.getText().toString().trim();
                            LastName = lastName.getText().toString().trim();
                            Email = email.getText().toString().trim();
                            PhoneNumber = phoneNumber.getText().toString().trim();
                            Password = password.getText().toString().trim();
                            City = city.getText().toString().trim();
                            // dateOfBirth=dob.getText().toString();
                        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        final OkHttpClient client = new OkHttpClient();
                        JSONObject postdata=new JSONObject();
                        try {
                            postdata.put ("firstName",FirstName);
                            postdata.put ("lastName",LastName);
                            postdata.put (	"email",Email);
                            postdata.put (	"gender",Gender);
                            postdata.put (		"city",City);
                            postdata.put (	"phone",PhoneNumber);
                            postdata.put (	"password",Password);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestBody body =RequestBody.create(JSON,postdata.toString());
                        Request request = new Request.Builder()
                                .url("https://finalscanme12.herokuapp.com/api/users")
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
                                try {
                                    JSONObject token=new JSONObject(responseData);
                                    if(token.has("msg")){
                                        Handler mainHandler = new Handler(Looper.getMainLooper());

                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), "Email already taken", Toast.LENGTH_SHORT).show();


                                            }
                                        });

                                    }else {
                                        Token = token.getString("id");
                                        Log.d("DEMO1233=", Token);

                                        // onComplete(Token);
                                        client.dispatcher().executorService().shutdown();
                                        Handler mainHandler = new Handler(Looper.getMainLooper());

                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar3.setVisibility(View.INVISIBLE);

                                                Toast.makeText(getContext(), "loged", Toast.LENGTH_SHORT).show();
                                                mListener.changeToHomeFragment(Token, FirstName
                                                        , LastName, Email, PhoneNumber,city.getText().toString());

                                            }
                                        });


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                }

                            });
                            //  onComplete(Token);


                    }


                }
            });



        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void changeToHomeFragment(String token, String firstName, String lastName, String email, String phoneNumber,String city);
    }
}
