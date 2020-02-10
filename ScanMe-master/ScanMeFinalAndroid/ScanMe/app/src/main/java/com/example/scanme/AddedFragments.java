package com.example.scanme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddedFragments.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddedFragments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddedFragments extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddedFragments() {
        // Required empty public constructor
    }
String token;
    TextView textView3;
    ArrayList<PojoUser> contactsList=new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter1;
    private RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar2;
    public AddedFragments(String token) {
        this.token=token;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddedFragments.
     */
    // TODO: Rename and change types and number of parameters
    public static AddedFragments newInstance(String param1, String param2) {
        AddedFragments fragment = new AddedFragments();
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
        setHasOptionsMenu(true);

        View view= inflater.inflate(R.layout.fragment_added_fragments, container, false);
        textView3=view.findViewById(R.id.textView8);
        textView3.setText("Loading...");
        progressBar2=view.findViewById(R.id.progressBar6);
        progressBar2.setVisibility(View.INVISIBLE);
        recyclerView= view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        getIds();
        return view;
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
                .url("https://finalscanme12.herokuapp.com/api/contact/Addedgetall")
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
                                textView3.setText("You Haven't met any one");
                                //textView6.setText("You Haven't met any one");
                            }else{
                                textView3.setText("");

                                //textView6.setText("");
                            }
                            for(int i=0;i<tokenA.length();i++){
                                try {
                                    progressBar2.setVisibility(View.VISIBLE);

                                    JSONObject con=((tokenA.getJSONObject(i)));
                                    getIndividual(con.getString("contactId"),con.getString("dateAdd"));
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
                            String flag="saved";
                            mAdapter1= new ConnectedUserAdapter(contactsList,getContext(),token,"saved");
                            recyclerView.setAdapter(mAdapter1);

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
//        MenuItem item = menu.findItem(R.id.home);
//        item.setVisible(false);
        MenuItem item1= menu.findItem(R.id.scan);
        item1.setVisible(false);
        MenuItem item2 = menu.findItem(R.id.share);
        item2.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit:
                getActivity(). getSupportFragmentManager().popBackStack();
                return true;
            case R.id.home:
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                getActivity(). startActivity(intent);
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
