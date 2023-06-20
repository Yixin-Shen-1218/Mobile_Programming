package com.example.UNISEA.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.ChatWindowActivity;
import com.example.UNISEA.FriendSearchResultActivity;
import com.example.UNISEA.Model.Chat;
import com.example.UNISEA.Model.FriendSearch;
import com.example.UNISEA.Model.User;
import com.example.UNISEA.OtherUserProfileActivity;
import com.example.UNISEA.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.ViewHolder>{
    private Context mContext;
    private List<User> FriendSearches;
    private String currentUid;

    public FriendSearchAdapter(Context context, List<User> friendSearches) {
        this.mContext = context;
        this.FriendSearches = friendSearches;
        this.currentUid = FirebaseAuth.getInstance().getUid();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RoundedImageView avatar;
        public TextView username;
        public Button add_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.Username);
            add_button = itemView.findViewById(R.id.add_button);
        }

        public void seeProfile(String uid, boolean isFriend) {
            Intent intent = new Intent(itemView.getContext(), OtherUserProfileActivity.class);
            intent.putExtra("uid",uid);
            intent.putExtra("isFriend",isFriend);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itemView.getContext().startActivity(intent);
        }
    }

    @NonNull
    @Override
    public FriendSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_item, parent, false);

        return new FriendSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendSearchAdapter.ViewHolder holder, int position) {

        final User friendSearch = FriendSearches.get(position);

        holder.username.setText(friendSearch.getUsername());
        Glide.with(mContext).load(friendSearch.getUrl()).into(holder.avatar);

        holder.add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(holder,friendSearch.getUid());

            }
        });

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               holder.seeProfile(friendSearch.getUid(), false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return FriendSearches.size();
    }

    private void sendRequest(@NonNull FriendSearchAdapter.ViewHolder holder, String uid) {
        Map<String, Object> request = new HashMap<>();
        FirebaseDatabase.getInstance().getReference("new_Users").child(currentUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                request.put(currentUid,user);

                FirebaseDatabase.getInstance().getReference("Requests").child(uid)
                        .updateChildren(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(mContext.getApplicationContext(), "Send success!",Toast.LENGTH_LONG).show();
                                holder.add_button.setVisibility(View.GONE);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
