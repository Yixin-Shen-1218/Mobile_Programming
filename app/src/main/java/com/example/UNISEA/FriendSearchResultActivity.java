package com.example.UNISEA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.UNISEA.Adapter.ChatAdapter;
import com.example.UNISEA.Adapter.FriendSearchAdapter;
import com.example.UNISEA.Model.Chat;
import com.example.UNISEA.Model.Contact;
import com.example.UNISEA.Model.FriendSearch;
import com.example.UNISEA.Model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FriendSearchResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FriendSearchAdapter FriendSearchAdapter;
    private List<User> FriendSearches;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search_result);

        Intent intent = getIntent();
        FriendSearches = (ArrayList<User>) intent.getSerializableExtra("results");


        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> onBackPressed());

        // Bind the recycler view
        recyclerView = findViewById(R.id.friend_search_result_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

        FriendSearchAdapter = new FriendSearchAdapter(this.getApplicationContext(), FriendSearches);
        recyclerView.setAdapter(FriendSearchAdapter);
        FriendSearchAdapter.notifyDataSetChanged();

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FriendSearchResultActivity.this, MainActivity.class);
        intent.putExtra("id",2);
        startActivity(intent);

        super.onBackPressed();
    }
}