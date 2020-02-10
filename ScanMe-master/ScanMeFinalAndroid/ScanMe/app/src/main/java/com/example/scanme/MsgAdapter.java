package com.example.scanme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    ArrayList<PojoMsg> msgs;

    public MsgAdapter(ArrayList<PojoMsg> msgs) {
        this.msgs = msgs;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.msg.setText(msgs.get(position).msg);
        holder.smsg.setText(msgs.get(position).smsg);
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg,smsg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.msg);
            smsg = itemView.findViewById(R.id.smsg);

        }
    }
}
