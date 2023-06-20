package com.example.UNISEA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.UNISEA.Adapter.PostAdapter;
import com.example.UNISEA.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class MySinglePostActivity extends AppCompatActivity {

    ArrayList<Post> posts;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private LinearLayout noPostLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_single_post);

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> onBackPressed());

        noPostLayout = findViewById(R.id.no_post_layout);

        Intent intent = getIntent();
        posts = (ArrayList<Post>)intent.getSerializableExtra("posts");
        if (posts.size() == 0) {
            noPostLayout.setVisibility(View.VISIBLE);
        }else {
            noPostLayout.setVisibility(View.GONE);
            recyclerView = findViewById(R.id.my_moment);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
            postAdapter = new PostAdapter(this.getApplicationContext(), posts, true);
            recyclerView.setAdapter(postAdapter);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MySinglePostActivity.this, MainActivity.class);
        intent.putExtra("id",5);
        startActivity(intent);
        super.onBackPressed();
    }

}