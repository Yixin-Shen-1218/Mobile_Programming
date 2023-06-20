package com.example.UNISEA.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.IOException;
import android.net.Uri;
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
import com.example.UNISEA.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private Context mContext;
    private List<Chat> Chats;


    public ChatAdapter(Context context, List<Chat> chats) {
        this.mContext = context;
        this.Chats = chats;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RoundedImageView avatar;
        public TextView username;
        public TextView latest_Chat;
        public TextView latest_chat_timestamp;
        public TextView unread_num;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.Username);
            latest_Chat = itemView.findViewById(R.id.Latest_Chat);
            latest_chat_timestamp = itemView.findViewById(R.id.Latest_Chat_Timestamp);
            unread_num = itemView.findViewById(R.id.unread_num);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);

        return new ChatAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Chat chat = Chats.get(position);

        Log.d("ChatAdapter", String.valueOf(chat.getUsername()));

        // TODO: set the avatar and other info
        Log.d("URL",chat.getImageurl());
//        Uri uri = Uri.parse(chat.getImageurl());
//        holder.avatar.setImageURI(uri);
//        holder.avatar.setImageBitmap(getBitmapFromURL(chat.getImageurl()));

        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(chat.getImageurl());
//        Glide.with(this.mContext).load(httpsReference).into(holder.avatar);
        Glide.with(this.mContext).load(chat.getImageurl()).into(holder.avatar);
//        try{
//            URL newurl = new URL(chat.getImageurl());
//            Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
//            holder.avatar.setImageBitmap(mIcon_val);
//        }
//        catch (IOException e){
//
//        }


        holder.username.setText(chat.getUsername());
        holder.latest_Chat.setText(chat.getLatest_chat());
        holder.latest_chat_timestamp.setText(chat.getTime_stamp());
        holder.unread_num.setText(chat.getUnread_num().toString());
        if (chat.getUnread_num() == 0){
            holder.unread_num.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ChatAdapter", String.valueOf(holder.getAdapterPosition()));
                Log.d("ChatAdapter", String.valueOf(holder.username.getText()));

                Intent intent=new Intent(view.getContext(), ChatWindowActivity.class);

                intent.putExtra("UID", chat.getUID());
                intent.putExtra("Username", chat.getUsername());
                intent.putExtra("Avatar", chat.getImageurl());

                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Chats.size();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}
