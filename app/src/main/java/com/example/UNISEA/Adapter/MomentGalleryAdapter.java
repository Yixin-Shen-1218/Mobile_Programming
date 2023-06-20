package com.example.UNISEA.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.MySinglePostActivity;
import com.example.UNISEA.R;

import java.util.ArrayList;
import java.util.List;


public class MomentGalleryAdapter extends RecyclerView.Adapter<MomentGalleryAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> moments;

    public MomentGalleryAdapter(Context context, List<Post> moments) {
        this.mContext = context;
        this.moments = moments;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView momentImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            momentImg = itemView.findViewById(R.id.moment_img);
        }
    }

    @NonNull
    @Override
    public MomentGalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.moment_item, parent, false);

        return new MomentGalleryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post moment = moments.get(position);
        Log.d("MomentGalleryAdapter", String.valueOf(moment.getImage()));

        String imgUrl = moment.getImage();
        Glide.with(mContext).load(imgUrl).into(holder.momentImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MomentGalleryAdapter", String.valueOf(holder.getAdapterPosition()));
                Log.d("MomentGalleryAdapter", String.valueOf(moment.getImage()));

                Intent intent=new Intent(view.getContext(), MySinglePostActivity.class);

                ArrayList<Post> moments = new ArrayList<>();
                moments.add(moment);

                intent.putExtra("posts", moments);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moments.size();
    }
}
