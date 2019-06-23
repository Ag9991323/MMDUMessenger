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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Locale;

public class FirestoreChatAdapter extends FirestoreRecyclerAdapter<MessageModel,FirestoreChatAdapter.holder> {
    Context context;
    String imageUrl;
    FirebaseUser mUser;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private static final int MSG_TYPE_LEFT=0;
    private  static final int MSG_TYPE_RIGHT=1;


    public FirestoreChatAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options,Context context,String imageUrl) {
        super(options);
        this.context=context;
        this.imageUrl=imageUrl;
    }

    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull MessageModel model) {
        String message = model.getMessage();
        String currentTime = model.getTimeStamp();


        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong("" + currentTime));
        String DateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.timeStamp.setText(DateTime);
        holder.chatMessage.setText(message);
//        if(ChatActivity.Image!=null) {
//            if (!ChatActivity.Image.isEmpty()) {
//                Picasso.get().load(ChatActivity.Image).placeholder(R.drawable.ic_profile_picture).into(holder.chatDp);
//            } else {
//                Picasso.get().load(R.drawable.ic_profile_picture).placeholder(R.drawable.ic_profile_picture).into(holder.chatDp);
//            }
//        }
//        else {
//            Picasso.get().load(R.drawable.ic_profile_picture).placeholder(R.drawable.ic_profile_picture).into(holder.chatDp);
//        }
            if(position==getItemCount()-1){
            if(model.getSeen()){
                holder.isSeen.setText("Seen");
            }
            else{
                holder.isSeen.setText("Delivered");
            }
        }
        else{
            holder.isSeen.setVisibility(View.GONE);
        }


    }
    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==MSG_TYPE_RIGHT){
            // Log.i("mmdumessage:",Integer.toString(i));
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.right_chat,viewGroup,false);
            return new holder(view);
        }
        else {
            //  Log.i("mmdumessage:",Integer.toString(i));

            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.left_chat,viewGroup,false);
            return new holder(view);
        }

    }
    @Override
    public int getItemViewType(int position) {
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        Log.i("mmdu messenger",Integer.toString(position));

        if(getItem(position).getSender().equals(mUser.getUid())){
            return MSG_TYPE_RIGHT;}
        else{
            return MSG_TYPE_LEFT;
        }

    }

//    @Override
//    public void onDataChanged() {
//        super.onDataChanged();
//
//    }

    public class holder extends RecyclerView.ViewHolder{
       // CircleImageView chatDp;
        TextView chatMessage,timeStamp,isSeen;
        public holder(@NonNull View itemView) {
            super(itemView);
          //  chatDp=itemView.findViewById(R.id.chatDp);
            chatMessage=itemView.findViewById(R.id.chatMessage);
            timeStamp=itemView.findViewById(R.id.timeStamp);
            isSeen=itemView.findViewById(R.id.isSeen);
        }
    }
}
