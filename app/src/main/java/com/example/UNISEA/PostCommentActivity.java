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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.UNISEA.Adapter.CommentListAdapter;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostCommentActivity extends AppCompatActivity {

    private ArrayList<Post> comments;
    private Post post;
    private String pid;
    private String uid;
    private User currentUser;

    private CommentListAdapter commentAdapter;

    private LinearLayout noDataLayout;
    private EditText comment;
    private Button postBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra("post");
        pid = post.getPid();
        uid = FirebaseAuth.getInstance().getUid();

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> onBackPressed());

        noDataLayout = findViewById(R.id.no_data_layout);
        comment = findViewById(R.id.edit_message);
        postBtn = findViewById(R.id.button_send);

        postBtn.setOnClickListener(v -> onPostClicked());
        comments = new ArrayList<>();

        getCurrentUser();

        display();
    }

    @Override
    public void onBackPressed() {
//        Intent returnIntent = new Intent();
//        setResult(RESULT_OK, returnIntent);
//        super.onBackPressed();

        Intent intent = new Intent(PostCommentActivity.this, MainActivity.class);
        intent.putExtra("id",4);
        startActivity(intent);
        super.onBackPressed();
    }

    private void display() {
        getCommentList();
    }

    private void getCommentList() {
        FirebaseDatabase.getInstance().getReference("Posts").child(pid)
                .child("comment").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Log.d(":!!!!", "onDataChange: " + snapshot1);
                            comments.add(snapshot1.getValue(Post.class));

                        }
                        if (snapshot.exists()) {
                            noDataLayout.setVisibility(View.GONE);
//                            displayCommits();
                        }
                        displayCommits();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayCommits() {
        RecyclerView commentList = findViewById(R.id.comment_list);
        commentList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        commentAdapter = new CommentListAdapter(getApplicationContext(), comments);
        commentList.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
    }

    private void getCurrentUser() {
        FirebaseDatabase.getInstance().getReference("new_Users").child(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        currentUser = task.getResult().getValue(User.class);
                    }
                });
    }

    private void onPostClicked() {
        String content = this.comment.getText().toString();
        Log.d("post", content);
        if (content.trim().equals("")) {
            Toast.makeText(getApplicationContext(),"You cannot post blank comment!",Toast.LENGTH_LONG).show();
            return;
        }
        String time = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date());
        long order = -System.currentTimeMillis();

        Post post = new Post();
        post.setContent(content);
        post.setTime(time);
        post.setOrder(order);
        post.setUid(FirebaseAuth.getInstance().getUid());
        comments.add(post);
        int count = comments.size();

        FirebaseDatabase.getInstance().getReference("Posts")
                .child(pid).child("comment").child(String.valueOf(count)).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        comment.setText("");
                        comment.setCursorVisible(false);
                        commentAdapter.notifyItemChanged(count -1);
                        Toast.makeText(getApplicationContext(),"Comment success!",Toast.LENGTH_LONG).show();
                        noDataLayout.setVisibility(View.GONE);
                    }
                });
    }
}