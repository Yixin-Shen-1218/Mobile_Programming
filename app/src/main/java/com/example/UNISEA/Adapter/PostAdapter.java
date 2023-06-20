package com.example.UNISEA.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.MainActivity;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.Model.User;
import com.example.UNISEA.MySinglePostActivity;
import com.example.UNISEA.PostCommentActivity;
import com.example.UNISEA.PostDetailActivity;
import com.example.UNISEA.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Set;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    private String uid;
    private final boolean isProfile;

    public PostAdapter(Context context, List<Post> posts, boolean isProfile) {
        this.context = context;
        this.posts = posts;
        this.uid = FirebaseAuth.getInstance().getUid();
        this.isProfile = isProfile;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView time;
        public TextView content;
        public TextView activityTag;
        public RoundedImageView image_profile;
        public LinearLayout momentIconsLayout;
        public LinearLayout activityIconsLayout;

        private ImageView image;
        private TextView momentDelBtn;
        private TextView activityDelBtn;
        private TextView activityCommentNum;
        private TextView momentCommentNum;
        private TextView activityGroupNum;
        private TextView momentLikeNum;

        private ImageView activityDetailIcon;
        private ImageView activityCommentIcon;
        private ImageView momentCommentIcon;
        private ImageView groupIcon;
        private ImageView likeIcon;

        public boolean isLike;
        public boolean isDelBtnVisible;
        private String posterUid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.textView_username);
            time = itemView.findViewById(R.id.textView_time);
            content = itemView.findViewById(R.id.textView_content);
            image_profile = itemView.findViewById(R.id.image_profile2);
            activityTag = itemView.findViewById(R.id.textView_activityTag);
            image = itemView.findViewById(R.id.post_image);
            momentDelBtn = itemView.findViewById(R.id.moment_delete_btn);
            activityDelBtn = itemView.findViewById(R.id.activity_delete_btn);
            activityDetailIcon = itemView.findViewById(R.id.activity_menu_icon);
            activityCommentIcon = itemView.findViewById(R.id.activity_comment_icon);
            momentCommentIcon = itemView.findViewById(R.id.moment_comment_icon);
            groupIcon = itemView.findViewById(R.id.activity_group_icon);
            likeIcon = itemView.findViewById(R.id.moment_like_icon);
            activityCommentNum = itemView.findViewById(R.id.activity_comment_num);
            activityGroupNum = itemView.findViewById(R.id.activity_group_num);
            momentLikeNum = itemView.findViewById(R.id.moment_like_num);
            momentCommentNum = itemView.findViewById(R.id.moment_comment_num);

            momentIconsLayout = itemView.findViewById(R.id.moment_icons);
            activityIconsLayout = itemView.findViewById(R.id.activity_icons);
        }

        public void postDetail(Post post, String uid) {
            Intent intent = new Intent(itemView.getContext(), PostDetailActivity.class);
            intent.putExtra("post", post);
            intent.putExtra("uid",uid);
            itemView.getContext().startActivity(intent);
        }

        public void commentPost(Post post) {
            Intent intent = new Intent(itemView.getContext(), PostCommentActivity.class);
            intent.putExtra("post", post);
            itemView.getContext().startActivity(intent);
        }

        public void deletePost(String pid) {
            FirebaseDatabase.getInstance().getReference("Posts").child(pid).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context.getApplicationContext(), "Delete success!",Toast.LENGTH_LONG).show();
                            if (isProfile) {
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }
                    });
        }

    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        String pid = post.getPid();
        String uid = post.getUid();

        // whether like this post
        List<String> likes = post.getLike();
        holder.isLike = likes.contains(this.uid);

        // fill in pre-stored username and avatar
        Glide.with(context).load(post.getAvatar()).into(holder.image_profile);
        holder.username.setText(post.getUsername());

        // whether own post
        holder.isDelBtnVisible = pid.startsWith(this.uid);

        // load updated username and avatar
        FirebaseDatabase.getInstance().getReference("new_Users").child(uid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context).load(user.getUrl()).into(holder.image_profile);
                holder.username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.time.setText(post.getTime());
        holder.content.setText(post.getContent());

        holder.activityDetailIcon.setOnClickListener(view -> {
            holder.postDetail(post, uid);
        });
        holder.activityCommentIcon.setOnClickListener(view -> {
            holder.commentPost(post);
        });
        holder.groupIcon.setOnClickListener(view -> {
            onGroupClicked(holder,pid);
        });
        holder.likeIcon.setOnClickListener(view -> {
            onLikeClicked(holder,pid);
        });
        holder.momentCommentIcon.setOnClickListener(view -> {
            holder.commentPost(post);
        });
        holder.activityDelBtn.setOnClickListener(view -> {
            holder.deletePost(pid);
        });
        holder.momentDelBtn.setOnClickListener(view -> {
            holder.deletePost(pid);
        });

        if (holder.isDelBtnVisible) {
            holder.activityDelBtn.setVisibility(View.VISIBLE);
            holder.momentDelBtn.setVisibility(View.VISIBLE);
        }else {
            holder.activityDelBtn.setVisibility(View.GONE);
            holder.momentDelBtn.setVisibility(View.GONE);
        }

        if (post.isActivity()) {
            holder.activityCommentNum.setText(String.valueOf(post.getCommentNum()));
            holder.activityGroupNum.setText(String.valueOf(post.getLike().size()));
            holder.activityTag.setVisibility(View.VISIBLE);
            holder.activityIconsLayout.setVisibility(View.VISIBLE);
            holder.momentIconsLayout.setVisibility(View.GONE);
            if (holder.isLike) {
                Glide.with(context).load(R.drawable.group_on).into(holder.groupIcon);
            } else {
                Glide.with(context).load(R.drawable.group).into(holder.groupIcon);
            }
        } else {
            holder.momentLikeNum.setText(String.valueOf(post.getLike().size()));
            holder.momentCommentNum.setText(String.valueOf(post.getCommentNum()));
            holder.activityTag.setVisibility(View.GONE);
            holder.momentIconsLayout.setVisibility(View.VISIBLE);
            holder.activityIconsLayout.setVisibility(View.GONE);
            if (holder.isLike) {
                Glide.with(context).load(R.drawable.heart_on).into(holder.likeIcon);
            } else {
                Glide.with(context).load(R.drawable.heart).into(holder.likeIcon);
            }
        }

        String imageUrl = post.getImage();
        if (imageUrl == null || imageUrl.equals("")) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context).load(imageUrl).into(holder.image);
        }


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void onLikeClicked(@NonNull ViewHolder holder, String pid) {
        if (holder.isLike) {
            FirebaseDatabase.getInstance().getReference("Posts")
                    .child(pid).child("like").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Glide.with(context).load(R.drawable.heart).into(holder.likeIcon);
                            holder.isLike = false;
                            if (isProfile) {
                                int num = Integer.parseInt(String.valueOf(holder.momentLikeNum.getText())) - 1;
                                holder.momentLikeNum.setText(String.valueOf(num));
                            }
                        }
                    });
        } else {
            FirebaseDatabase.getInstance().getReference("Posts").child(pid)
                    .child("like").child(uid).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Glide.with(context).load(R.drawable.heart_on).into(holder.likeIcon);
                            holder.isLike = true;
                            if (isProfile) {
                                int num = Integer.parseInt(String.valueOf(holder.momentLikeNum.getText())) + 1;
                                holder.momentLikeNum.setText(String.valueOf(num));
                            }
                        }
                    });
        }
    }

    private void onGroupClicked(@NonNull ViewHolder holder, String pid) {
        if (holder.isLike) {
            FirebaseDatabase.getInstance().getReference("Posts")
                    .child(pid).child("like").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Glide.with(context).load(R.drawable.group).into(holder.groupIcon);
                            holder.isLike = false;
                        }
                    });
        } else {
            FirebaseDatabase.getInstance().getReference("Posts").child(pid)
                    .child("like").child(uid).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Glide.with(context).load(R.drawable.group_on).into(holder.groupIcon);
                            holder.isLike = true;
                        }
                    });

        }
    }

}
