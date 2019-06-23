package com.example.mmdumessenger.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mmdumessenger.R;
import com.example.mmdumessenger.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.holder>  {
    private static final int MSG_TYPE_LEFT=0;
    private  static final int MSG_TYPE_RIGHT=1;
    private static final String TAG = "FirestoreAdapter";

    List<MessageModel> chats_messagelist;
    Context context;
    String imageUrl;
    FirebaseUser mUser;

    public ChatAdapter(Context context, List<MessageModel> chats_messagelist, String imageUrl) {
        this.chats_messagelist = chats_messagelist;
        this.context = context;
        this.imageUrl=imageUrl;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i==MSG_TYPE_RIGHT){
           // Log.i("mmdumessage:",Integer.toString(i));
            View view= LayoutInflater.from(context).inflate(R.layout.right_chat,viewGroup,false);
            return new holder(view);
        }
        else {
          //  Log.i("mmdumessage:",Integer.toString(i));

            View view= LayoutInflater.from(context).inflate(R.layout.left_chat,viewGroup,false);
            return new holder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int i) {
        String message=chats_messagelist.get(i).getMessage();
        String currentTime= chats_messagelist.get(i).getTimeStamp();



             Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
             calendar.setTimeInMillis(Long.parseLong(""+currentTime));
             String DateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
             holder.timeStamp.setText(DateTime);
             holder.chatMessage.setText(message);

//
//        if(!TextUtils.isEmpty(imageUrl)){
//            try{
//
//                Glide.with(context).asBitmap().load(imageUrl).into(holder.chatDp);
//                Toast.makeText(context,"try wala"+imageUrl,Toast.LENGTH_SHORT).show();
//                // Picasso.get().load(Image).into(myholder.userImage);
//            }
//            catch (Exception e){
//                Toast.makeText(context,"catch wala"+imageUrl,Toast.LENGTH_SHORT).show();
//                Glide.with(context).asBitmap().load(R.drawable.ic_profile_picture).into(holder.chatDp);
//                //Picasso.get().load(R.drawable.ic_profile).into(myholder.userImage);
//            }
//
//        }
//        else{
//            Glide.with(context).asBitmap().load(R.drawable.ic_profile_picture).into(holder.chatDp);
//        }



//        try{
//            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_profile_picture).into(holder.chatDp);
//
//        }catch (Exception e){
//            Picasso.get().load(R.drawable.ic_profile_picture).into(holder.chatDp);
//        }
        if(i==chats_messagelist.size()-1){
            try {
                if(chats_messagelist.get(i).getSeen()){
                    holder.isSeen.setText("Seen");}

                else{
                    holder.isSeen.setText("delivered");
                }



            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            holder.isSeen.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return chats_messagelist.size();
    }

    @Override
    public int getItemViewType(int position) {
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        Log.i("mmdu messenger",Integer.toString(position));
        if(chats_messagelist.get(position).getSender().equals(mUser.getUid())){
            return MSG_TYPE_RIGHT;}
        else{
            return MSG_TYPE_LEFT;
        }

    }




    public class holder extends RecyclerView.ViewHolder{
   // CircleImageView chatDp;
    TextView chatMessage,timeStamp,isSeen;
    public holder(@NonNull View itemView) {
        super(itemView);
        //chatDp=itemView.findViewById(R.id.chatDp);
        chatMessage=itemView.findViewById(R.id.chatMessage);
        timeStamp=itemView.findViewById(R.id.timeStamp);
        isSeen=itemView.findViewById(R.id.isSeen);
    }}}
