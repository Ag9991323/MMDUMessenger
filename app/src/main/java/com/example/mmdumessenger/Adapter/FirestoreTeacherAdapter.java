package com.example.mmdumessenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mmdumessenger.ChatActivity;
import com.example.mmdumessenger.R;
import com.example.mmdumessenger.models.Users;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirestoreTeacherAdapter  extends FirestoreRecyclerAdapter<Users,FirestoreTeacherAdapter.myholder> {
    FirebaseAuth mAuth;
    Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirestoreTeacherAdapter(@NonNull FirestoreRecyclerOptions<Users> options,Context context) {
        super(options);
        mAuth=FirebaseAuth.getInstance();
        this.context=context;
    }



        @Override
        protected void onBindViewHolder (@NonNull myholder holder,int position, @NonNull Users model){
          FirebaseUser user = mAuth.getCurrentUser();


            final String hisUid=model.getUid();
            String Name = model.getName();
            String Image = model.getImage();

            holder.userName.setText(Name);
            if(!TextUtils.isEmpty(Image)){
                try{

                    Glide.with(context).asBitmap().load(Image).into(holder.userImage);
                    Toast.makeText(context,"try wala"+Image,Toast.LENGTH_SHORT).show();
                    // Picasso.get().load(Image).into(myholder.userImage);
                }
                catch (Exception e){
                    Toast.makeText(context,"catch wala"+Image,Toast.LENGTH_SHORT).show();
                    Glide.with(context).asBitmap().load(R.drawable.ic_profile).into(holder.userImage);
                    //Picasso.get().load(R.drawable.ic_profile).into(myholder.userImage);
                }

            }
            else{
                Glide.with(context).asBitmap().load(R.drawable.ic_profile).into(holder.userImage);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, ChatActivity.class);
                    intent.putExtra("hisUid",hisUid);
                    context.startActivity(intent);
                }
            });



        }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_recycle_view, viewGroup, false);

        return new myholder(view);
    }

    public class myholder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView userName;

        public myholder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
        }
    }

}





