package com.example.UNISEA.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.UNISEA.Adapter.PostAdapter;
import com.example.UNISEA.BottomSheetDialog.PostBottomSheetDialog;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {
    private PostAdapter postAdapter;
    private List<Post> posts;
    private ProgressBar refresh;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        refresh = view.findViewById(R.id.refresh);
        TextView postText = view.findViewById(R.id.PostText);
        postText.setOnClickListener(v -> onPostClick());

        RecyclerView recyclerView = view.findViewById(R.id.demo_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts, false);
        recyclerView.setAdapter(postAdapter);

        loadPosts();
        refreshPosts();

        return view;
    }

    private void refreshPosts() {
       FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("order").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        posts.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren()) {
                            Post post = snapshot1.getValue(Post.class);
                            long comment = snapshot1.child("comment").getChildrenCount();
                            post.setCommentNum(comment);
                            posts.add(post);
                        }
                        savePosts(posts);
                        postAdapter.setPosts(posts);
                        postAdapter.notifyDataSetChanged();
                        refresh.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void savePosts(List<Post> posts) {
        try {
            String filename = "posts.data";
            FileOutputStream fos = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(posts);
            os.close();
        }  catch (IOException e) {
            refreshPosts();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPosts() {
        try {
            String filename = "posts.data";
            FileInputStream fis = getContext().openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            List<Post> posts = (List<Post>) is.readObject();
            postAdapter.setPosts(posts);
            postAdapter.notifyDataSetChanged();
            is.close();
        } catch (IOException | ClassNotFoundException e) {
            Toast.makeText(getContext(),"No stored posts found.",Toast.LENGTH_LONG).show();
        }
    }

    public void onPostClick() {
        Log.d("icon_test", "onPostClick");
        PostBottomSheetDialog bottomSheet = new PostBottomSheetDialog();
        bottomSheet.show(getParentFragmentManager(),
                "ModalBottomSheet");

    }
}