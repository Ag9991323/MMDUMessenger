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
import com.example.mmdumessenger.models.MessageModel;
import com.example.mmdumessenger.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserschatAdapter extends RecyclerView.Adapter<UserschatAdapter.myholder> implements Filterable {
    Context mContext;
    List<Users> usersList;
    private List<Users> usersListFull;
    private boolean isOnline;
    String theLastMessage;


    public UserschatAdapter(Context mContext, List<Users> usersList, boolean isOnline) {

        this.mContext = mContext;
        this.usersList = usersList;
        usersListFull=new ArrayList<>(usersList);
        this.isOnline =isOnline;
    }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.userschatrecycleview,viewGroup,false);

        return new myholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myholder myholder, int i) {
       final String hisUid =usersList.get(i).getUid();
        String Name = usersList.get(i).getName();
        String Image = usersList.get(i).getImage();


        myholder.userName.setText(Name);
        if(!TextUtils.isEmpty(Image)){
            try{

                Glide.with(mContext).asBitmap().load(Image).into(myholder.userImage);

                // Picasso.get().load(Image).into(myholder.userImage);
            }
            catch (Exception e){

                Glide.with(mContext).asBitmap().load(R.drawable.ic_profile).into(myholder.userImage);
                //Picasso.get().load(R.drawable.ic_profile).into(myholder.userImage);
            }

        }
        else{
            Glide.with(mContext).asBitmap().load(R.drawable.ic_profile).into(myholder.userImage);
        }

            lastMessage(hisUid,myholder.last_msg);

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
        private  TextView last_msg;

        public myholder(@NonNull View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.userImage);
            userName=itemView.findViewById(R.id.userName);
            img_off =itemView.findViewById(R.id.img_off);
            img_on = itemView.findViewById(R.id.img_on);
            last_msg=itemView.findViewById(R.id.last_msg);
        }
    }
    //check for last msg
    public void lastMessage(String userId, final TextView last_msg){
        theLastMessage ="default";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query chatsQuery= FirebaseFirestore.getInstance().collection("Chats").document(user.getUid()).collection("messages").document("all_message").collection(userId).orderBy("timeStamp", Query.Direction.DESCENDING).limit(1);
         chatsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
             @Override
             public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                 if(!queryDocumentSnapshots.isEmpty()){
                     for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){

                         MessageModel messageModel = doc.getDocument().toObject(MessageModel.class);
                         theLastMessage=messageModel.getMessage();


                     }
                 }
                 switch(theLastMessage){
                     case "default":
                         last_msg.setText("No message");
                         break;

                     default:
                         last_msg.setText(theLastMessage);
                         break;
                 }
                 theLastMessage="default";
             }
         });
    }
}
