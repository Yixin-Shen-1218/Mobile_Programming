package com.example.UNISEA.Fragment;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.BlockedNumberContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Adapter.MomentGalleryAdapter;
import com.example.UNISEA.MainActivity;
import com.example.UNISEA.Model.Blocked;
import com.example.UNISEA.Model.Contact;
import com.example.UNISEA.Model.LocationPos;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.Model.Preference;
import com.example.UNISEA.Model.Profile;
import com.example.UNISEA.Model.User;
import com.example.UNISEA.R;
import com.example.UNISEA.Util.LocationHelper;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchFragment extends Fragment{
    private final static int COL_NUM = 3;

    private String uid;

    private RecyclerView recyclerView;
    private MomentGalleryAdapter mgAdapter;

    private ImageView accept;
    private ImageView decline;
    private TextView userName;
    private TextView age;
    private TextView university;
    private TextView bio;
    private ImageView avator;
    private ImageView sexIcon;
    private TextView id_match;
    private TextView url;
    private TextView distance;

    TagFlowLayout tagFlowLayout;
    private LayoutInflater layoutInflater;
    private List<String> tags;
    private List<Post> moments;
    private User user;
    private Profile profile;
    private LocationPos myLoc;
    private LocationPos matchLoc;
    private View view;
    private View view2;
    private Preference perference;
    private Profile matchProfile;
    private ArrayList<String> ideal_matches = new ArrayList<>();
    private ScrollView Match_View;
    private ConstraintLayout Zero_Match;



    public MatchFragment() {
        // Required empty public constructor
    }

    public static MatchFragment newInstance() {
        MatchFragment fragment = new MatchFragment();
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
        view = inflater.inflate(R.layout.fragment_match, container, false);

        Match_View = view.findViewById(R.id.Match_View);
        Zero_Match = view.findViewById(R.id.Zero_Match);
        Zero_Match.setVisibility(View.GONE);

        // Bind the accept and decline  with OnClick
        accept = view.findViewById(R.id.accept);
        decline = view.findViewById(R.id.decline);
        accept.setOnClickListener(v -> onAcceptClick());
        decline.setOnClickListener(v -> addBlocked(false));

        // Bind the recycler view
        recyclerView = view.findViewById(R.id.moments_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), COL_NUM));

        userName = view.findViewById(R.id.profile_username);
        age = view.findViewById(R.id.profile_age);
        university = view.findViewById(R.id.profile_university);
        distance = view.findViewById(R.id.distance);

        id_match = view.findViewById(R.id.id_match);
        id_match.setVisibility(View.GONE);

        url = view.findViewById(R.id.url);
        url.setVisibility(View.GONE);

        bio = view.findViewById(R.id.about_me_text);
        avator = view.findViewById(R.id.profile_avatar);
        sexIcon = view.findViewById(R.id.profile_sex_icon);

        tags = new ArrayList<>();
        moments = new ArrayList<>();
        mgAdapter = new MomentGalleryAdapter(getContext(), moments);
        recyclerView.setAdapter(mgAdapter);

        uid = FirebaseAuth.getInstance().getUid();
        findMatch();

        return view;
    }


    private void readPreference(ArrayList<String> matches, ArrayList<String> blocked, ArrayList<String> contacts) {
        FirebaseDatabase.getInstance().getReference("Preference").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int found = 1;

                        perference = snapshot.getValue(Preference.class);

                        String currentMatchId;
                        Log.d("test_matches_findOneMatch", matches.toString());
                        Log.d("test_matches_findOneMatch", blocked.toString());

                        for (int i=0; i<matches.size(); i++) {
                            found = 1;

//                            if (blocked != null) {
//
//                            }
                            for (String blockUser : blocked) {
                                if (matches.get(i).equals(blockUser)) {
                                    found = 0;
                                    break;
                                }
                            }

                            if (contacts != null) {
                                for (String contactUser : contacts) {
                                    if (matches.get(i).equals(contactUser)) {
                                        found = 0;
                                        break;
                                    }
                                }
                            }


                            if (matches.get(i).equals(uid)) {
                                found = 0;
                            }

                            if (found == 1) {
                                ideal_matches.add(matches.get(i));
                            }
                        }
                        int current_index = 0;

                        Log.d("ideal_matches",ideal_matches.toString());
                        readMatchProfile(current_index);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }


    private void readMatchProfile(int current_index) {
        Log.d("current_index", new Integer(current_index).toString());
        if (current_index == ideal_matches.size()) {
            readUser("");
            return;
        }
        FirebaseDatabase.getInstance().getReference("Profile").child(ideal_matches.get(current_index))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int found = 1;
                        matchProfile = snapshot.getValue(Profile.class);

                        if (Objects.isNull(perference)) {
                            found = 2;
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                        try {
                            if (found == 1 && !Objects.isNull(perference.getAge1())&& !Objects.isNull(perference.getAge2()) && !Objects.isNull(matchProfile.getBirth())) {
                                Date birthDate = sdf.parse(matchProfile.getBirth());
                                Date date = new Date(System.currentTimeMillis());
                                long age = (date.getTime() - birthDate.getTime()) / (1000L * 3600 * 24 * 365);
                                if (perference.getAge1() > age && perference.getAge2() < age) {
                                    found = 0;
                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (found == 1 && !Objects.isNull(perference.isFemale()) && !Objects.isNull(perference.isMale()) && !Objects.isNull(perference.isNonBinary())) {
                            if (matchProfile.getGender().equals("Female") && !perference.isFemale()) {
                                found = 0;
                            } else if (matchProfile.getGender().equals("Male") && !perference.isMale()) {
                                found = 0;
                            } else if (matchProfile.getGender().equals("No-binary") && !perference.isNonBinary()) {
                                found = 0;
                            }
                        }

                        if (found == 1 && !Objects.isNull(perference.getHeight1())&& !Objects.isNull(perference.getHeight2()) && !Objects.isNull(matchProfile.getHeight())) {
                            if (matchProfile.getHeight() > perference.getHeight2() || matchProfile.getHeight() < perference.getHeight1()) {
                                found = 0;
                            }
                        }


                        if (found == 1 || found ==2) {
                            Log.d("test_found", ideal_matches.get(current_index).toString());
                            id_match.setText(ideal_matches.get(current_index).toString());

                            readUser(ideal_matches.get(current_index));

                        } else {
                            readMatchProfile(current_index+1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void findMatch() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Profile");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot
                ArrayList<String> curr_matches =  collect((Map<String,Object>) dataSnapshot.getValue());
                findBlocked(curr_matches);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });
    }



    private void findBlocked(ArrayList<String> curr_matches) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("BlockedMatch").child(uid);

        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        ArrayList<String> blocked = collect((Map<String,Object>) dataSnapshot.getValue());
                        findContact(curr_matches, blocked);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


    private void findContact(ArrayList<String> curr_matches, ArrayList<String> blocked) {

       FirebaseDatabase.getInstance().getReference("Friends").child(uid)
               .addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        ArrayList<String> contacts = collect((Map<String,Object>) dataSnapshot.getValue());
                        readPreference(curr_matches, blocked, contacts);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


    private ArrayList<String> collect(Map<String,Object> users) {

        ArrayList<String> uids = new ArrayList<>();

        if (users == null) {
            return null;
        }
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            String singleUser = entry.getKey();
            //Get phone field and append to list
            uids.add(singleUser);


        }
        return uids;
    }



    private void readUser(String currentMatchUid) {
        if (currentMatchUid.equals("")) {
//            FragmentTransaction fr = getFragmentManager().beginTransaction();
//            fr.replace(R.id.container,new ZeroMatchFragment());
//            fr.addToBackStack(null);
//            fr.commit();

//            // Setting for Fragments
//            Fragment zeroMatchFragment = ZeroMatchFragment.newInstance();
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.layout_navbar, chatsFragment)
//                    .addToBackStack(null)
//                    .commit();
//
//            ButterKnife.bind(this);

            Match_View.setVisibility(View.INVISIBLE);
            Zero_Match.setVisibility(View.VISIBLE);
            accept.setVisibility(View.INVISIBLE);
            decline.setVisibility(View.INVISIBLE);
            return;
        }


        FirebaseDatabase.getInstance().getReference("new_Users").child(currentMatchUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                        readProfile(currentMatchUid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void readProfile(String currentMatchUid) {
        FirebaseDatabase.getInstance().getReference("Profile").child(currentMatchUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        profile = snapshot.getValue(Profile.class);
                        readMatchLocation(currentMatchUid);
//                        fillProfile(currentMatchUid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void readMatchLocation(String currentMatchUid) {
        FirebaseDatabase.getInstance().getReference("Location").child(currentMatchUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        matchLoc = snapshot.getValue(LocationPos.class);
                        readCurUserLocation(currentMatchUid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void readCurUserLocation(String currentMatchUid) {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("Location").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myLoc = snapshot.getValue(LocationPos.class);
                        fillProfile(currentMatchUid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    @SuppressLint("RestrictedApi")
    public void onAcceptClick() {
        Map<String, Object> request = new HashMap<>();
        FirebaseDatabase.getInstance().getReference("new_Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        request.put(uid,user);
                        String matchId = id_match.getText().toString();
                        FirebaseDatabase.getInstance().getReference("Requests").child(matchId)
                                .updateChildren(request);
                        addBlocked(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addBlocked(boolean isAccept) {
        Blocked new_block = new Blocked(id_match.getText().toString());
        FirebaseDatabase.getInstance().getReference("BlockedMatch").child(uid).child(id_match.getText()
                .toString()).setValue(new_block).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (isAccept) {
                    Toast.makeText(getContext(), "Send success!",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Refused!",Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("id", 3);
                startActivity(intent);
            }
        });
    }

    private void fillProfile(String currentMatchUid) {
        if (myLoc == null || matchLoc == null) {
            distance.setText("unknown");
        } else {
            String dist = LocationHelper.calculateDistanceInKilometer(myLoc, matchLoc);
            distance.setText(dist + "km");
        }


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
        this.url.setText(url);

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
        if (!(url == null)) {
            Glide.with(getContext()).load(url).into(avator);
        }


        initTagsView();
        readMoments(currentMatchUid);
    }

    private void initTagsView() {
        layoutInflater = LayoutInflater.from(getContext());
        tagFlowLayout = view.findViewById(R.id.id_flowlayout);

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

    private void readMoments(String currentMatchUid) {

        Log.d("id_test", currentMatchUid);

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = postRef.orderByChild("uid").equalTo(currentMatchUid);
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