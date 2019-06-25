package com.example.mmdumessenger.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mmdumessenger.R;
import com.example.mmdumessenger.models.Posts;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter  extends RecyclerView.Adapter<PostAdapter.holder>{
    Context mContext;
    List<Posts> postList;

    public PostAdapter(Context mContext, List<Posts> postList) {
        this.mContext = mContext;
        this.postList = postList;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_recycle_view,viewGroup,false);

        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final holder holder, int i) {
        String uid = postList.get(i).getUid();
        String email = postList.get(i).getEmail();
        String date = postList.get(i).getDate();
        String time = postList.get(i).getTime();
        String title = postList.get(i).getTitle();
        String description = postList.get(i).getDescription();
        final String dp = postList.get(i).getDp();
        final String postImage = postList.get(i).getPostImage();
        String name = postList.get(i).getName();

        holder.userNameTv.setText(name);
        holder.postTitleTv.setText(title);
        holder.postdescriptionTv.setText(description);
        holder.postTimeTv.setText(time);
        holder.postDateTv.setText(date);

        if (!TextUtils.isEmpty(dp)) {
            try {
                Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).into(holder.userImageIv, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(dp).into(holder.userImageIv);

                    }
                });
            } catch (Exception e) {
                Picasso.get().load(R.drawable.ic_profile).into(holder.userImageIv);
            }

        } else {
            Glide.with(mContext).asBitmap().load(R.drawable.ic_profile).into(holder.userImageIv);
        }


        if (postImage.equals("noImage")) {
            holder.postImageIv.setVisibility(View.GONE);

        } else {
            try {
                Picasso.get().load(postImage).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(holder.postImageIv, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(postImage).into(holder.postImageIv);

                    }
                });
            }
            catch (Exception e){

            }


        }
    }
    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class holder extends RecyclerView.ViewHolder{

        CircleImageView userImageIv;
        ImageView  postImageIv;
        TextView   userNameTv,postDateTv,postTimeTv,postTitleTv,postdescriptionTv;
        public holder(@NonNull View itemView) {
            super(itemView);
            userImageIv=itemView.findViewById(R.id.userImageIv);
            postImageIv=itemView.findViewById(R.id.postImageIv);
            userNameTv=itemView.findViewById(R.id.userNameTv);
            postDateTv=itemView.findViewById(R.id.postDateTv);
            postTimeTv=itemView.findViewById(R.id.postTimeTv);
            postTitleTv=itemView.findViewById(R.id.postTitleTv);
            postdescriptionTv=itemView.findViewById(R.id.postDescriptionTv);
        }
    }
}
