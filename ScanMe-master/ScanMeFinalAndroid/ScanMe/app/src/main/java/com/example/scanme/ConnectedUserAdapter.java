package com.example.scanme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectedUserAdapter  extends RecyclerView.Adapter<ConnectedUserAdapter.ViewHolder> {
 ArrayList<PojoUser> userList;
    private Context context;
    String token;
    String flag;

    public ConnectedUserAdapter(ArrayList<PojoUser> userList, Context context, String token, String flag) {
        this.userList = userList;
        this.context = context;
        this.token=token;
        this.flag=flag;

    }

    @NonNull
    @Override
    public ConnectedUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.connected_user, parent, false);
        ConnectedUserAdapter.ViewHolder viewHolder = new ConnectedUserAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ConnectedUserAdapter.ViewHolder holder,  int position) {
        holder.name.setText(userList.get(position).name);
        holder.phoneNumber.setText(userList.get(position).phoneNumber);
        String primDate=userList.get(position).date;
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date newDate=spf.parse(primDate);
            spf= new SimpleDateFormat("MM dd yyyy HH:mm aa");
            primDate = spf.format(newDate);
            holder.date.setText(primDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

//        holder.date.setText(userList.get(position).date);
        holder.city.setText(userList.get(position).city);
        final int pos=position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callingApi(userList.get(pos).userId);
                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
// Sets the MIME type to match the Contacts Provider
                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, userList.get(pos).email)
                            .putExtra(ContactsContract.Intents.Insert.PHONE, userList.get(pos).phoneNumber)
                            .putExtra(ContactsContract.Intents.Insert.NAME,userList.get(pos).name);

                    context. startActivity(intent);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete User?");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview((ZXingScannerView.ResultHandler) getContext());
                            dialog.dismiss();

                        }

                    });
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview((ZXingScannerView.ResultHandler) getContext());
                            delete(userList.get(pos).userId,pos);

                        }
                    });
                    //builder.setMessage("Are S");
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                    return true;
                }
            });
    }


    private void callingApi(String tokenId) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://finalscanme12.herokuapp.com/api/contact/add");
        Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show();

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
                       // progressBar2.setVisibility(View.INVISIBLE);
                        //Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                    }
                });


            }

        });
    }

    public void delete(String Id, final int pos){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();
        try {

            postdata.put("userId", Id);
            postdata.put("contactId", token);
            postdata.put("flag", flag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, postdata.toString());
        Request request = new Request.Builder()
                .url("https://finalscanme12.herokuapp.com/api/contact/delete")
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
                     userList.remove(pos);
                     notifyDataSetChanged();
                    }
                });

            }

        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phoneNumber, date, city;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        name = itemView.findViewById(R.id.textViewUserName);
        phoneNumber=itemView.findViewById(R.id.textViewPhoneNumber);
        date= itemView.findViewById(R.id.textViewDate);
        city=itemView.findViewById(R.id.textViewCity);

        }

    }
}
