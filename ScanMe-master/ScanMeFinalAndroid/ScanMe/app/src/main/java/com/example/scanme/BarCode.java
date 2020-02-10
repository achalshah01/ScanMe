package com.example.scanme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BarCode extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    Button button3;
    TextView textView6,textView3;
 ArrayList<PojoUser> contactsList=new ArrayList<>();
    ImageView Qr;
    String name,email,phone, token,city,qId;
    ArrayList<PojoUser> getConnectedUserList=new ArrayList<PojoUser>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar2;


    private OnFragmentInteractionListener mListener;

    public BarCode(String name, String email, String phone, String token,String city,String qId) {
        this.name=name;
        this.phone=phone;
        this.email=email;
        this.token=token;
        this.city=city;
        this.qId=qId;
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
        getActivity().setTitle("ScanMe - Home");
        setHasOptionsMenu(true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        name=preferences.getString("name","");
        email=preferences.getString("email","");
        phone=preferences.getString("phone","");
        token= preferences.getString("token", "");
        View view= inflater.inflate(R.layout.fragment_bar_code, container, false);
        Qr=view.findViewById(R.id.barcode);
        progressBar2=view.findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.INVISIBLE);
        button3=view.findViewById(R.id.button3);
        textView6=view.findViewById(R.id.textView6);
        textView3=view.findViewById(R.id.textView3);
        textView3.setText("Loading...");
        checking();
        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
        try {
            BitMatrix bitMatrix=multiFormatWriter.encode(name+" "+email+" "+phone+" "+token, BarcodeFormat.QR_CODE,1000,400);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
            Qr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
//


        recyclerView= view.findViewById(R.id.userlistview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        getIds();
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getIds();
                contactsList.clear();
            }
        });

        return view;
    }

    private void checking() {
       // Toast.makeText(getContext(), "No bewt", Toast.LENGTH_SHORT).show();
        Log.d("demo111111111111111111111111111133333333333===",qId);

        if(qId.length() >0) {
            String [] idsss=qId.split(" ");
            Log.d("demo1qqqqqqqqqqqqqqq",idsss.toString());
           // Toast.makeText(getContext(), "Updating", Toast.LENGTH_SHORT).show();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("qId","");
            editor.apply();
            if (isNetworkAvailable()) {

                for (int i = 0; i < idsss.length; i++) {
                    progressBar2.setVisibility(View.VISIBLE);
                    callingApi(idsss[i].trim());
                }
            }
        }
    }


    private void callingApi(String tokenId) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://finalscanme12.herokuapp.com/api/contact/add");
      //  Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();

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
                String responseData = response.body().string();

                Log.d("DEMO123=", responseData);

                // onComplete(Token);
                client.dispatcher().executorService().shutdown();
                Handler mainHandler = new Handler(Looper.getMainLooper());

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // mListener.changeToHomeFragment(Token);
                        progressBar2.setVisibility(View.INVISIBLE);
                        //Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                    }
                });


            }

        });
    }



    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    private void getIds() {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();
        try {

            postdata.put("contactId", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, postdata.toString());
        Request request = new Request.Builder()
                .url("https://finalscanme12.herokuapp.com/api/contact/getall")
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
                    final JSONArray tokenA = new JSONArray(responseData);


                        Log.d("DEMO1233=", token.toString());

                        // onComplete(Token);
                        client.dispatcher().executorService().shutdown();
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(tokenA.length()==0){
                                    textView3.setText("");
                                    textView6.setText("You Haven't met any one");
                                }else{
                                    textView3.setText("");

                                    textView6.setText("");
                                }
                                for(int i=0;i<tokenA.length();i++){
                                    try {
                                        progressBar2.setVisibility(View.VISIBLE);

                                        JSONObject con=((tokenA.getJSONObject(i)));
                                        getIndividual(con.getString("userId"),con.getString("dateAdd"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.d("ccccpntact",contactsList.toString());
                               // mAdapter= new ConnectedUserAdapter(getConnectedUserList);
                                //recyclerView.setAdapter(mAdapter);

                            }
                        });



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }


   public void getIndividual(String id, final String dateAdd){
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();
        try {

            postdata.put("userId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, postdata.toString());
        Request request = new Request.Builder()
                .url("https://finalscanme12.herokuapp.com/api/contact/getOne")
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
                    final JSONObject tokeno = new JSONObject(responseData);
                     PojoUser pojoUser=new PojoUser();
                     pojoUser.setUserId(tokeno.getString("_id"));
                    pojoUser.setName(tokeno.getString("firstName")+" "+tokeno.getString("lastName"));
                    pojoUser.setEmail(tokeno.getString("email"));
                    pojoUser.setGender(tokeno.getString("gender"));
                    pojoUser.setCity(tokeno.getString("city"));
                    pojoUser.setPhoneNumber(tokeno.getString("phone"));
                    pojoUser.setDate(dateAdd);
                    if(contactsList.size()>0) {
                        for (int i = 0; i < contactsList.size(); i++) {
                            if (contactsList.get(i).getEmail().contentEquals(pojoUser.getEmail())) {

                            } else {
                                contactsList.add(pojoUser);
                                Collections.sort(contactsList, new Comparator<PojoUser>() {
                                    @Override
                                    public int compare(PojoUser o1, PojoUser o2) {
                                        return o1.getDate().compareTo(o2.getDate());
                                    }
                                });
                                break;
                            }
                        }
                    }
                    else{
                        contactsList.add(pojoUser);

                    }

                    Log.d("demosss", String.valueOf(contactsList.size()));
                    Log.d("demosss1111111111111", String.valueOf(contactsList.get(0).getUserId()));
                    Handler mainHandler = new Handler(Looper.getMainLooper());

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            progressBar2.setVisibility(View.INVISIBLE);

                             mAdapter= new ConnectedUserAdapter(contactsList,getContext(),token, "contact");
                            recyclerView.setAdapter(mAdapter);

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.home);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit:
                getActivity(). getSupportFragmentManager().beginTransaction().addToBackStack("l")
                        .replace(R.id.container, new EditDetails(token,phone,name,city))
                        .commit();
                return true;
            case R.id.scan:
                getActivity(). getSupportFragmentManager().beginTransaction().addToBackStack("l")
                        .replace(R.id.container, new ScanBarcode())
                        .commit();
                return true;
            case R.id.share:
                getActivity(). getSupportFragmentManager().beginTransaction().addToBackStack("list")
                        .replace(R.id.container, new ScanAndShare())
                        .commit();
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




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
