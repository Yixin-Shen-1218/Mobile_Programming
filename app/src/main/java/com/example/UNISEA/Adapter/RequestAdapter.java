package com.example.UNISEA.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Model.User;
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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{
    private Context mContext;
    private List<User> RequestUid;
    private String currentUid;

    public RequestAdapter(Context context, List<User> requests) {
        this.mContext = context;
        this.RequestUid = requests;
        this.currentUid = FirebaseAuth.getInstance().getUid();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RoundedImageView avatar;
        public TextView username;
        public ImageView MatchAgree;
        public ImageView MatchDisagree;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.Username);
            MatchAgree = itemView.findViewById(R.id.MatchAgree);
            MatchDisagree = itemView.findViewById(R.id.MatchDisagree);
        }
    }


    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.request_item, parent, false);

        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {

        final User other = RequestUid.get(position);

        holder.username.setText(other.getUsername());
        Glide.with(mContext).load(other.getUrl()).into(holder.avatar);

        holder.MatchAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact(other, currentUid);
                addMe(other.getUid());
                holder.itemView.setVisibility(View.GONE);
            }
        });

        holder.MatchDisagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRequest(other.getUid(),false);
                holder.itemView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return RequestUid.size();
    }

    private void addContact(User user, String uid) {
        Map<String,Object> contact = new HashMap<>();
        contact.put(user.getUid(),user);
        FirebaseDatabase.getInstance().getReference("Friends").child(uid)
                .updateChildren(contact);
    }

    private void addMe(String uid) {
        FirebaseDatabase.getInstance().getReference("new_Users").child(currentUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User me = snapshot.getValue(User.class);
                addContact(me,uid);
                deleteRequest(uid,true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void deleteRequest(String uid, boolean accept) {
        FirebaseDatabase.getInstance().getReference("Requests").child(currentUid)
                .child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (accept) {
                            Toast.makeText(mContext,"Add success!",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext,"Refuse success!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
