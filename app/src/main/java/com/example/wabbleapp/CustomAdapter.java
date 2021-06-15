package com.example.wabbleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends  RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    Context context;
    ArrayList<User_details> UserInfo;

    public CustomAdapter(Context context1,ArrayList<User_details> UserData){
        context = context1;
        UserInfo = UserData;
    }

    @NonNull
    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.commom_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        holder.fullname.setText(UserInfo.get(position).getFullname());
        holder.email.setText(UserInfo.get(position).getEmail());
        holder.phoneNo.setText(UserInfo.get(position).getPhoneNo());
    }


    @Override
    public int getItemCount() {
        return UserInfo.size();
    }
    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView fullname,email,phoneNo;
        public CustomViewHolder(@NonNull View itemView){
            super(itemView);
            fullname=itemView.findViewById(R.id.common_user_name);
            email=itemView.findViewById(R.id.common_user_email);
            phoneNo=itemView.findViewById(R.id.common_user_mobile);

        }
    }

}
