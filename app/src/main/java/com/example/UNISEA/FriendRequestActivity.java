package com.example.UNISEA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.UNISEA.Adapter.ChatRecordsAdapter;
import com.example.UNISEA.Adapter.RequestAdapter;
import com.example.UNISEA.Fragment.ContactsFragment;
import com.example.UNISEA.Model.Chat;
import com.example.UNISEA.Model.ChatRecord;
import com.example.UNISEA.Model.Request;
import com.example.UNISEA.Model.User;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestActivity extends AppCompatActivity {
    private ImageView backButton;
    private RecyclerView recyclerView;
    private LinearLayout noRequestLayout;
    private ArrayList<User> Requests;
    private RequestAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        backButton = findViewById(R.id.BackBtn);
        backButton.setOnClickListener(v -> onBackPressed());

        noRequestLayout = findViewById(R.id.no_request_layout);

        recyclerView = findViewById(R.id.recycler_view_request);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

        Intent intent = getIntent();
        Requests = (ArrayList<User>) intent.getSerializableExtra("requests");

        if (Requests.size() == 0) {
            noRequestLayout.setVisibility(View.VISIBLE);
        } else {
            noRequestLayout.setVisibility(View.GONE);
        }

        requestAdapter = new RequestAdapter(this.getApplicationContext(), Requests);
        recyclerView.setAdapter(requestAdapter);
        requestAdapter.notifyDataSetChanged();

    }


    // Return to the ContactsFragment
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FriendRequestActivity.this, MainActivity.class);
        intent.putExtra("id",2);
        startActivity(intent);
        super.onBackPressed();
    }
}