package com.example.UNISEA.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.UNISEA.ChatWindowActivity;
import com.example.UNISEA.Model.Chat;
import com.example.UNISEA.Model.Step;
import com.example.UNISEA.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder>{
    private Context mContext;
    private List<Step> Steps;

    public StepAdapter(Context context, List<Step> steps) {
        this.mContext = context;
        this.Steps = steps;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RoundedImageView avatar;
        public TextView username;
        public TextView rank;
        public TextView step_num;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.username);
            rank = itemView.findViewById(R.id.rank);
            step_num = itemView.findViewById(R.id.step_num);
        }
    }

    @NonNull
    @Override
    public StepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.step_item, parent, false);

        return new StepAdapter.ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StepAdapter.ViewHolder holder, int position) {

        final Step step = Steps.get(position);

        Log.d("StepAdapter", String.valueOf(step.getUsername()));

        // TODO: set the avatar and other info
//        holder.avatar.setImageURI(chat.getImageurl());
        holder.username.setText(step.getUsername());
        holder.step_num.setText(step.getStep_num().toString());
        holder.rank.setText(step.getRank().toString());
    }

    @Override
    public int getItemCount() {
        return Steps.size();
    }
}
