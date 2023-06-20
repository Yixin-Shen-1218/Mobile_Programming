package com.example.UNISEA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Adapter.MomentGalleryAdapter;
import com.example.UNISEA.BottomSheetDialog.DeleteFriendBottomSheetDialog;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.Model.Profile;
import com.example.UNISEA.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OtherUserProfileActivity extends AppCompatActivity {
    private final static int COL_NUM = 3;

    private String uid;
    private boolean isFriend;

    private LinearLayout profileView;
    private LinearLayout distanceView;
    private RecyclerView recyclerView;
    private MomentGalleryAdapter mgAdapter;

    private ImageView backButton;
    private TextView userName;
    private TextView age;
    private TextView university;
    private TextView bio;
    private ImageView avator;
    private ImageView sexIcon;
    private TextView messageTv;
    private TextView deleteContactTv;
    private TextView distance;

    TagFlowLayout tagFlowLayout;
    private LayoutInflater layoutInflater;
    private List<String> tags;
    private List<Post> moments;
    private User user;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        Intent intent = getIntent();
        isFriend = intent.getBooleanExtra("isFriend", false);
        uid = intent.getStringExtra("uid");
        Log.d("friend", String.valueOf(isFriend));
        Log.d("uid", uid);

        bindView();
        readUser();
    }

    private void bindView() {
        backButton = findViewById(R.id.BackBtn);
        backButton.setOnClickListener(v -> onBackPressed());
        messageTv = findViewById(R.id.message);
        messageTv.setOnClickListener(v -> onMessageClicked());
        deleteContactTv = findViewById(R.id.delete_contact);
        deleteContactTv.setOnClickListener(v -> onDeleteClicked());

        profileView = findViewById(R.id.others_profile_container);
        // Bind the recycler view
        recyclerView = profileView.findViewById(R.id.moments_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), COL_NUM));

        userName = profileView.findViewById(R.id.profile_username);
        age = profileView.findViewById(R.id.profile_age);
        university = profileView.findViewById(R.id.profile_university);
        bio = profileView.findViewById(R.id.about_me_text);
        avator = profileView.findViewById(R.id.profile_avatar);
        sexIcon = profileView.findViewById(R.id.profile_sex_icon);
        distanceView = profileView.findViewById(R.id.distance_layout);
        distance = profileView.findViewById(R.id.distance_textview);

        if (isFriend) {
            messageTv.setVisibility(View.VISIBLE);
            deleteContactTv.setVisibility(View.VISIBLE);
            distanceView.setVisibility(View.GONE);
        }else {
            messageTv.setVisibility(View.GONE);
            deleteContactTv.setVisibility(View.GONE);
            distanceView.setVisibility(View.VISIBLE);

            // TODO: get the distance between me and this user
            distance.setText("1.5km");
        }

        tags = new ArrayList<>();
        moments = new ArrayList<>();
        mgAdapter = new MomentGalleryAdapter(OtherUserProfileActivity.this, moments);
        recyclerView.setAdapter(mgAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    private void onMessageClicked() {
        Log.d("click", "message clicked");
        Intent intent=new Intent(this, ChatWindowActivity.class);

        intent.putExtra("UID", user.getUid());
        intent.putExtra("Username", user.getUsername());
        intent.putExtra("Avatar", user.getUrl());
        startActivity(intent);
    }

    private void onDeleteClicked() {
        Log.d("click", "delete clicked");
        String myId = FirebaseAuth.getInstance().getUid();
        DeleteFriendBottomSheetDialog bottomSheet = new DeleteFriendBottomSheetDialog(myId, uid);
        bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
    }

    private void readUser() {
        FirebaseDatabase.getInstance().getReference("new_Users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                        readProfile();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void readProfile() {
        FirebaseDatabase.getInstance().getReference("Profile").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        profile = snapshot.getValue(Profile.class);
                        fillProfile();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void fillProfile() {
        String username = user.getUsername();
        String url = user.getUrl();

        String gender = profile.getGender();
        String university = profile.getUniversity();

        String birth = profile.getBirth();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date birthDate = sdf.parse(birth);
            Date date = new Date(System.currentTimeMillis());
            long age = (date.getTime() - birthDate.getTime()) / (1000L * 3600 * 24 * 365);
            this.age.setText(String.valueOf(age));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String about = profile.getAbout();
        String tag1 = profile.getTag1();
        String tag2 = profile.getTag2();
        String tag3 = profile.getTag3();
        String tag4 = profile.getTag4();

        this.userName.setText(username);
        this.university.setText(university);
        this.bio.setText(about);

        tags.clear();
        if (!tag1.equals("")) {
            tags.add(tag1);
        }
        if (!tag2.equals("")) {
            tags.add(tag2);
        }
        if (!tag3.equals("")) {
            tags.add(tag3);
        }
        if (!tag4.equals("")) {
            tags.add(tag4);
        }

        if (gender.equals("Male")) {
            sexIcon.setImageResource(R.drawable.mars);
        } else if (gender.equals("Female")) {
            sexIcon.setImageResource(R.drawable.venus);
        } else {
            sexIcon.setImageResource(R.drawable.non_binary);
        }

        Glide.with(getApplicationContext()).load(url).into(avator);

        initTagsView();
        readMoments();
    }

    private void initTagsView() {
        layoutInflater = LayoutInflater.from(getApplicationContext());
        tagFlowLayout = profileView.findViewById(R.id.id_flowlayout);

        tagFlowLayout.setAdapter(new TagAdapter<String>(tags)
        {
            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) layoutInflater.inflate(R.layout.tag_layout,
                        tagFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });
    }

    private void readMoments() {
        Log.d("id_test", uid);

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = postRef.orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    moments.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // moments with an image
                        boolean isMoment = !snapshot.child("activity").getValue(Boolean.class);
                        String imgUrl = snapshot.child("image").getValue(String.class);
                        boolean withImg = imgUrl != null && !imgUrl.equals("");

                        if (isMoment && withImg) {
                            Post post = snapshot.getValue(Post.class);
                            moments.add(post);
                        }
                    }
                    mgAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}