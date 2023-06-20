package com.example.UNISEA.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.IOException;
import android.net.Uri;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Model.ChatRecord;
import com.example.UNISEA.OtherUserProfileActivity;
import com.example.UNISEA.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.net.URL;
import java.util.List;

public class ChatRecordsAdapter extends RecyclerView.Adapter<ChatRecordsAdapter.ViewHolder>{
    private Context mContext;
    private List<ChatRecord> ChatRecords;
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;
    private static final int TYPE_LEFT_IMG = 2;
    private static final int TYPE_RIGHT_IMG = 3;

    public ChatRecordsAdapter(Context mContext, List<ChatRecord> chatRecords) {
        this.mContext = mContext;
        this.ChatRecords = chatRecords;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RoundedImageView avatar;
        public TextView records;
        public ImageView img;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            records = itemView.findViewById(R.id.records);
            img = itemView.findViewById(R.id.img);
        }

        public void getItemViewType(int i) {

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view_left = LayoutInflater.from(mContext).inflate(R.layout.left_chat, parent, false);
        View view_right = LayoutInflater.from(mContext).inflate(R.layout.right_chat, parent, false);
//        View view_left_img = LayoutInflater.from(mContext).inflate(R.layout.left_chat_img, parent, false);
//        View view_right_img = LayoutInflater.from(mContext).inflate(R.layout.right_chat_img, parent, false);
        Log.d("ChatWindowViewTypeviewType", String.valueOf(viewType));

        switch (viewType){
            case TYPE_LEFT:
            case TYPE_LEFT_IMG:
                return new ChatRecordsAdapter.ViewHolder(view_left);
            case TYPE_RIGHT:
            case TYPE_RIGHT_IMG:
                return new ChatRecordsAdapter.ViewHolder(view_right);
        }

        return new ChatRecordsAdapter.ViewHolder(view_left);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChatRecord chatRecord = ChatRecords.get(position);

        Glide.with(this.mContext).load(chatRecord.getImageurl()).into(holder.avatar);

        Integer viewType = chatRecord.getType();

        if (viewType == TYPE_LEFT || viewType == TYPE_RIGHT){
            // TODO: Change the records text
            holder.records.setText(chatRecord.getRecords());
            Log.d("ChatRecords", chatRecord.getRecords());
            holder.img.setVisibility(View.GONE);
        }else{
            Log.d("GLIDEURL",chatRecord.getRecords());
            Glide.with(this.mContext).load(chatRecord.getRecords()).into(holder.img);
            holder.records.setVisibility(View.GONE);
        }

        // TODO: click and jump to user's profile
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OtherUserProfileActivity.class);
                intent.putExtra("isFriend", true);

                intent.putExtra("uid", chatRecord.getUID());
                view.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return ChatRecords.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return ChatRecords.size();
    }

    public void updateRecords(ChatRecord record){
        this.ChatRecords.add(record);
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


