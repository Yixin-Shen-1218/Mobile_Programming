package com.example.UNISEA.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Model.User;
import com.example.UNISEA.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeListAdapter extends RecyclerView.Adapter<LikeListAdapter.ViewHolder> {
    private Context context;
    private List<User> users;

    public LikeListAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public CircleImageView image_profile;
        public TextView fullName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            image_profile = itemView.findViewById(R.id.image_profile);
//            fullName = itemView.findViewById(R.id.fullname);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

//        holder.fullName.setVisibility(View.GONE);
        holder.username.setText(user.getUsername());
        Glide.with(context).load(user.getUrl()).into(holder.image_profile);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
