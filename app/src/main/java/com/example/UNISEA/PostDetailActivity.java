package com.example.UNISEA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Adapter.LikeListAdapter;
import com.example.UNISEA.Model.ActivityDetail;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class PostDetailActivity extends AppCompatActivity {

    private Post post;
    private String pid;
    private String uid;

    private LikeListAdapter likeAdapter;
    private ArrayList<User> likes;

    TextView groupNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra("post");
        pid = post.getPid();
        uid = intent.getStringExtra("uid");

        likes = new ArrayList<>();

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> onBackPressed());

        getLikeList();

        display();
    }

    @SuppressLint("SetTextI18n")
    private void display() {
        View postView = findViewById(R.id.post_item_layout);
        RoundedImageView image_profile = postView.findViewById(R.id.image_profile2);
        TextView username = postView.findViewById(R.id.textView_username);
        TextView content = postView.findViewById(R.id.textView_content);
        ImageView image = postView.findViewById(R.id.post_image);
        LinearLayout momentIcons = postView.findViewById(R.id.moment_icons);
        LinearLayout activityIcons = postView.findViewById(R.id.activity_icons);

        TextView time = findViewById(R.id.textView_time);
        TextView dateTime = findViewById(R.id.textView_date_time_detail);
        TextView location = findViewById(R.id.textView_location_detail);
        groupNum = findViewById(R.id.group_num);

        String imageUrl = post.getImage();
        if (imageUrl == null || imageUrl.equals("")) {
            image.setVisibility(View.GONE);
        } else {
            Glide.with(getApplicationContext()).load(imageUrl).into(image);
        }
        momentIcons.setVisibility(View.GONE);
        activityIcons.setVisibility(View.GONE);

        FirebaseDatabase.getInstance().getReference("new_Users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Glide.with(getApplicationContext()).load(user.getUrl()).into(image_profile);
                        username.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


        time.setText(post.getTime());
        content.setText(post.getContent());

        ActivityDetail details = post.getDetails();
        dateTime.setText(details.getDate() + " " + details.getStartTime() + "-" + details.getEndTime());
        location.setText(details.getLocation());


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("return", "Hello from the second activity.");
//        setResult(RESULT_OK, returnIntent);
        Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
        intent.putExtra("id",4);
        startActivity(intent);
        super.onBackPressed();
    }

    private void getLikeList() {
        FirebaseDatabase.getInstance().getReference("Posts").child(pid)
                .child("like").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String uid = snapshot1.getKey();
                            Log.d("why", uid);
                            mapUidToUser(uid);
                        }
                        displayLikes();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void mapUidToUser(String uid) {
        FirebaseDatabase.getInstance().getReference("new_Users").child(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        User user = task.getResult().getValue(User.class);
                        likes.add(user);
                        likeAdapter.notifyDataSetChanged();
                        groupNum.setText(String.valueOf(likes.size()));
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayLikes() {
        RecyclerView likeList = findViewById(R.id.group_list);
        likeList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        likeAdapter = new LikeListAdapter(getApplicationContext(), likes);
        likeList.setAdapter(likeAdapter);
        likeAdapter.notifyDataSetChanged();

    }

}