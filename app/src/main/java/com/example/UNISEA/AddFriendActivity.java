package com.example.UNISEA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.example.UNISEA.Model.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddFriendActivity extends AppCompatActivity {

    TextInputLayout friendSearchField;
    Button friendSearchBtn;

    private String currentUid;
    private List<String> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        friendSearchField = findViewById(R.id.friend_search);
        friendSearchBtn = findViewById(R.id.friend_search_btn);
        currentUid = FirebaseAuth.getInstance().getUid();

        friendSearchBtn.setOnClickListener(v -> onSearchPressed());
        friends = new ArrayList<>();
        getFriends();

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        Log.d("icon_test", "onAddFriendClick ");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("return", "Hello from the second activity.");
        setResult(RESULT_OK, returnIntent);

        super.onBackPressed();
    }

    private void getFriends() {
        FirebaseDatabase.getInstance().getReference("Friends").child(currentUid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    friends.add(child.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void onSearchPressed() {
        String username = friendSearchField.getEditText().getText().toString().trim();
        ArrayList<User> results = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("new_Users").orderByChild("username")
                .startAt(username).endAt(username+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        results.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren()) {
                            User user = snapshot1.getValue(User.class);
                            if (Objects.equals(user.getUid(), currentUid)) {
                                continue;
                            }
                            if (friends.contains(user.getUid())) {
                                continue;
                            }
                            results.add(user);
                        }
                        Log.d("TAG", "onDataChange: " + friends);
                        Intent intent = new Intent(AddFriendActivity.this, FriendSearchResultActivity.class);
                        intent.putExtra("results",results);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });



    }

}