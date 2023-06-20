package com.example.UNISEA.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.analyzer.VerticalWidgetRun;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Adapter.MomentGalleryAdapter;
import com.example.UNISEA.EditMatchProfileActivity;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.Model.Preference;
import com.example.UNISEA.Model.Profile;
import com.example.UNISEA.Model.User;
import com.example.UNISEA.MySinglePostActivity;
import com.example.UNISEA.ProfileEditActivity;
import com.example.UNISEA.R;
import com.example.UNISEA.StepActivity;
import com.example.UNISEA.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment{
    private final static int COL_NUM = 3;

    private String uid;

    private LinearLayout profileView;
    private LinearLayout distanceView;
    private RecyclerView recyclerView;
    private MomentGalleryAdapter mgAdapter;

    private TextView editText;
    private TextView userName;
    private TextView age;
    private TextView university;
    private TextView bio;
    private ImageView avator;
    private ImageView sexIcon;
    private TextView myActivitiesTv;
    private TextView stepTv;
    private TextView logOutTv;

    TagFlowLayout tagFlowLayout;
    private LayoutInflater layoutInflater;
    private List<String> tags;
    private ArrayList<Post> moments;
    private ArrayList<Post> activities;
    private User user;
    private Profile profile;

    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Bind the postText text with OnClick
        editText = view.findViewById(R.id.EditText);
        editText.setOnClickListener(v -> onEditClick());

        myActivitiesTv = view.findViewById(R.id.my_activities);
        myActivitiesTv.setOnClickListener(v -> onMyActivitiesClicked());

        stepTv = view.findViewById(R.id.steps);
        stepTv.setOnClickListener(v -> onStepsClicked());

        logOutTv = view.findViewById(R.id.log_out);
        logOutTv.setOnClickListener(v -> onLogOutClicked());

        // Bind the profile view
        profileView = view.findViewById(R.id.profile_container);

        // Bind the recycler view
        recyclerView = profileView.findViewById(R.id.moments_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), COL_NUM));

        userName = profileView.findViewById(R.id.profile_username);
        age = profileView.findViewById(R.id.profile_age);
        university = profileView.findViewById(R.id.profile_university);
        bio = profileView.findViewById(R.id.about_me_text);
        avator = profileView.findViewById(R.id.profile_avatar);
        sexIcon = profileView.findViewById(R.id.profile_sex_icon);
        distanceView = profileView.findViewById(R.id.distance_layout);
        distanceView.setVisibility(View.GONE);

        tags = new ArrayList<>();
        moments = new ArrayList<>();
        activities = new ArrayList<>();
        mgAdapter = new MomentGalleryAdapter(getContext(), moments);
        recyclerView.setAdapter(mgAdapter);

        uid = FirebaseAuth.getInstance().getUid();
        readUser();

        return view;
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


    public void onEditClick() {
        Log.d("icon_test", "onEditClick");
        Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
        startActivity(intent);
    }

    private void onMyActivitiesClicked() {
        // TODO: show my activities
        Log.d("click", "activities");
        Intent intent = new Intent(getActivity(), MySinglePostActivity.class);
        intent.putExtra("posts", activities);
        startActivity(intent);
    }

    // Navigate to the step page to see friends' daily steps
    private void onStepsClicked() {
        Intent intent = new Intent(getActivity(), StepActivity.class);
        startActivity(intent);
    }

    private void onLogOutClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Log out:");
        builder.setMessage("Do you really want to log out?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), WelcomeActivity.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void fillProfile() {
        String username = user.getUsername();
        String url = user.getUrl();

        String gender = profile.getGender();
        String university = profile.getUniversity();

        String birth = profile.getBirth();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
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

        Glide.with(getContext()).load(url).into(avator);

        initTagsView();
        readMoments();
    }

    private void initTagsView() {
        layoutInflater = LayoutInflater.from(getContext());
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
                        if (!isMoment) {
                            Post post = snapshot.getValue(Post.class);
                            activities.add(post);
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