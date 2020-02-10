package com.example.scanme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.IntentCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String name,phone, token,city;
    EditText firstName;
    EditText lastName;
    EditText phoneNumber;
    EditText cityEdit;
    Button Update;
    ProgressBar progressBar4;
    private OnFragmentInteractionListener mListener;

    public EditDetails() {
        // Required empty public constructor
    }

    public EditDetails(String token, String phone, String name, String city) {
        this.name=name;
        this.phone=phone;
        this.token=token;
        this.city=city;
    }



    public static EditDetails newInstance(String param1, String param2) {
        EditDetails fragment = new EditDetails();
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
        getActivity().setTitle("ScanMe Edit details");
        setHasOptionsMenu(true);

        View view= inflater.inflate(R.layout.fragment_edit_details, container, false);
        firstName=view.findViewById(R.id.editFirstName1);
        lastName=view.findViewById(R.id.editLastName1);
        phoneNumber=view.findViewById(R.id.PhoneNumber1);
        progressBar4=view.findViewById(R.id.progressBar4);
        progressBar4.setVisibility(View.INVISIBLE);
        cityEdit=view.findViewById(R.id.cty1);
        Update=view.findViewById(R.id.button21);
        String [] split= name.split(" ");
        firstName.setText(split[0]);
        lastName.setText(split[1]);
        phoneNumber.setText(phone);
        cityEdit.setText(city);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstName.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Enter First Name", Toast.LENGTH_SHORT).show();
                    firstName.setError("This field can not be blank");
                } else if (lastName.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Enter Last Name", Toast.LENGTH_SHORT).show();
                    lastName.setError("This field can not be blank");
                }else if (cityEdit.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Enter city", Toast.LENGTH_SHORT).show();
                    cityEdit.setError("This field can not be blank");
                }
                else if (phoneNumber.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Enter phone number", Toast.LENGTH_SHORT).show();
                    phoneNumber.setError("This field can not be blank");
                } else {
                    progressBar4.setVisibility(View.VISIBLE);

                    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    final OkHttpClient client = new OkHttpClient();
                    JSONObject postdata = new JSONObject();
                    try {
                        postdata.put ("userId",token);
                        postdata.put ("firstName",firstName.getText().toString());
                        postdata.put ("lastName",lastName.getText().toString());
                        postdata.put (		"city",cityEdit.getText().toString());
                        postdata.put (	"phone",phoneNumber.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody body = RequestBody.create(JSON, postdata.toString());
                    Request request = new Request.Builder()
                            .url("https://finalscanme12.herokuapp.com/api/contact/update")
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
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar4.setVisibility(View.INVISIBLE);

                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("name",firstName.getText().toString()+" "+ lastName.getText().toString());
                                    editor.putString("phone",phoneNumber.getText().toString());
                                    editor.putString("city",cityEdit.getText().toString());
                                    editor.apply();
                                    getActivity(). getSupportFragmentManager().popBackStack();


                                }
                            });
                        }

                    });

                }

            }
        });

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.logout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token","");
                editor.putString("email","");
                editor.putString("name","");
                editor.putString("phone","");
                editor.putString("city","");
                editor.apply();
                mListener.logout();
                return true;
            case R.id.Contacts:
                getActivity(). getSupportFragmentManager().beginTransaction().addToBackStack("list")
                        .replace(R.id.container, new AddedFragments(token))
                        .commit();
            default:
                return super.onOptionsItemSelected(item);
        }
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

        void logout();
    }
}
