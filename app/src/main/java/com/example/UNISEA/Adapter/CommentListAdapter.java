package com.example.UNISEA.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.Model.User;
import com.example.UNISEA.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    private Context context;
    private List<Post> comments;

    public CommentListAdapter(Context context, List<Post> comments) {
        this.context = context;
        this.comments = comments;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView image;
        public TextView username;
        public TextView time;
        public TextView tag;
        public TextView content;
        public ImageView imageView;
        public LinearLayout activityIcons;
        public LinearLayout momentIcons;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_profile2);
            username = itemView.findViewById(R.id.textView_username);
            time = itemView.findViewById(R.id.textView_time);
            tag = itemView.findViewById(R.id.textView_activityTag);
            content = itemView.findViewById(R.id.textView_content);
            imageView = itemView.findViewById(R.id.post_image);
            activityIcons = itemView.findViewById(R.id.activity_icons);
            momentIcons = itemView.findViewById(R.id.moment_icons);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post comment = comments.get(position);
        String uid = comment.getUid();

        FirebaseDatabase.getInstance().getReference("new_Users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Glide.with(context).load(user.getUrl()).into(holder.image);
                        holder.username.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        holder.time.setText(comment.getTime());
        holder.tag.setVisibility(View.GONE);
        holder.content.setText(comment.getContent());
        holder.imageView.setVisibility(View.GONE);
        holder.activityIcons.setVisibility(View.GONE);
        holder.momentIcons.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
