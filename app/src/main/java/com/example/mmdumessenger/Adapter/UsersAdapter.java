package com.example.mmdumessenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mmdumessenger.ChatActivity;
import com.example.mmdumessenger.R;
import com.example.mmdumessenger.models.Users;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter  extends RecyclerView.Adapter<UsersAdapter.myholder> implements Filterable {
    Context mContext;
    List<Users> usersList;
    private List<Users> usersListFull;
    private boolean isOnline;

    public UsersAdapter( Context mContext, List<Users> usersList,boolean isOnline) {

        this.mContext = mContext;
        this.usersList = usersList;
        usersListFull=new ArrayList<>(usersList);
        this.isOnline =isOnline;
    }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_recycle_view,viewGroup,false);

        return new myholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final myholder myholder, int i) {
        final String hisUid=usersList.get(i).getUid();
        String Name = usersList.get(i).getName();
        final String Image = usersList.get(i).getImage();


        myholder.userName.setText(Name);
        if(!TextUtils.isEmpty(Image)){
            try{

//
                Picasso.get().load(Image).networkPolicy(NetworkPolicy.OFFLINE).into(myholder.userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Image).into(myholder.userImage);

                    }
                });

                // Picasso.get().load(Image).into(myholder.userImage);
            }
            catch (Exception e){

               Picasso.get().load(R.drawable.ic_profile).into(myholder.userImage);
                //Picasso.get().load(R.drawable.ic_profile).into(myholder.userImage);
            }

        }
        else{
            Glide.with(mContext).asBitmap().load(R.drawable.ic_profile).into(myholder.userImage);
        }
     Log.i("adapterdepartment",usersList.get(i).getName() +isOnline);

        if(isOnline){
            if(usersList.get(i).getOnline()){
                myholder.img_on.setVisibility(View.VISIBLE);
                myholder.img_off.setVisibility(View.GONE);
            }
            else{
                myholder.img_on.setVisibility(View.GONE);
                myholder.img_off.setVisibility(View.VISIBLE);
            }
        }
        else {
            myholder.img_on.setVisibility(View.GONE);
            myholder.img_off.setVisibility(View.GONE);
        }
        myholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mContext, ChatActivity.class);
                intent.putExtra("hisUid",hisUid);
                mContext.startActivity(intent);
            }
        });


        }





    @Override
    public int getItemCount() {
        return usersList.size();
    }

    @Override
    public Filter getFilter() {
        return userlistFilter;
    }
    private Filter userlistFilter =new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Users> FilteredList =new ArrayList<>();
            if(constraint ==null||constraint.length()==0){
                FilteredList.addAll(usersListFull);
            }
            else {
                String filterPattern=constraint.toString().toLowerCase().trim();
                for(Users  item : usersListFull){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        FilteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values=FilteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
          usersList.clear();
          usersList.addAll((List)results.values);
          notifyDataSetChanged();
        }
    };

    public class myholder extends RecyclerView.ViewHolder{

        CircleImageView userImage;
        TextView userName;
        private CircleImageView img_on;
        private CircleImageView img_off;

        public myholder(@NonNull View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.userImage);
            userName=itemView.findViewById(R.id.userName);
            img_off =itemView.findViewById(R.id.img_off);
            img_on = itemView.findViewById(R.id.img_on);
        }
    }
}
