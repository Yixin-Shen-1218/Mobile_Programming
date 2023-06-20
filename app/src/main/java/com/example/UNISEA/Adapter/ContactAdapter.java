package com.example.UNISEA.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.ChatWindowActivity;
import com.example.UNISEA.Model.Chat;
import com.example.UNISEA.Model.Contact;
import com.example.UNISEA.Model.User;
import com.example.UNISEA.OtherUserProfileActivity;
import com.example.UNISEA.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private Context mContext;
    private List<User> Contacts;


    public ContactAdapter(Context context, List<User> cntacts) {
        this.mContext = context;
        this.Contacts = cntacts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RoundedImageView avatar;
        public TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.Username);
        }
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_item, parent, false);

        return new ContactAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {

        final User contact = Contacts.get(position);

        holder.username.setText(contact.getUsername());
        Glide.with(mContext).load(contact.getUrl()).into(holder.avatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ChatAdapter", String.valueOf(holder.getAdapterPosition()));
                Log.d("ChatAdapter", String.valueOf(holder.username.getText()));

                Intent intent=new Intent(view.getContext(), OtherUserProfileActivity.class);
                intent.putExtra("isFriend", true);

                intent.putExtra("uid", contact.getUid());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Contacts.size();
    }
}
