//package com.example.UNISEA.Fragment;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.SearchView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.UNISEA.Adapter.ContactAdapter;
//import com.example.UNISEA.AddFriendActivity;
//import com.example.UNISEA.FriendRequestActivity;
//import com.example.UNISEA.Model.Contact;
//import com.example.UNISEA.Model.User;
//import com.example.UNISEA.R;
//import com.example.UNISEA.RegisterActivity;
//import com.example.UNISEA.WelcomeActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link ContactsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class ContactsFragment extends Fragment {
//    private ImageView addFriend;
//    private androidx.appcompat.widget.SearchView searchView;
//    private TextView friend_request;
//    private TextView friend_request_num;
//    private LinearLayout noFriendLayout;
//    private RecyclerView recyclerView;
//    private ContactAdapter contactAdapter;
//    private List<User> Contacts;
//    private String uid;
//    private ArrayList<User> requests;
//
//    public ContactsFragment() {
//        // Required empty public constructor
//    }
//
//    public static ContactsFragment newInstance() {
//        ContactsFragment fragment = new ContactsFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
//
//        uid = FirebaseAuth.getInstance().getUid();
//
//        //TODO: remove this test
////        uid = "3vheCH6jreaIhfgRuSlHTQvXc032";
//
//        // Bind the addFriend image with OnClick
//        addFriend = view.findViewById(R.id.AddFriend);
//        addFriend.setOnClickListener(v -> onAddFriendClick());
//
//        // TODO: complete the search listening algorithm
//        searchView = view.findViewById(R.id.searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchContact(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (newText.equals("")) {
//                    readContacts();
//                } else {
//                    searchContact(newText);
//                }
//                return false;
//            }
//        });
//
//        friend_request = view.findViewById(R.id.friend_request);
//        friend_request.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(view.getContext(), FriendRequestActivity.class);
//                intent.putExtra("requests",requests);
//                startActivity(intent);
//            }
//        });
//
//        friend_request_num = view.findViewById(R.id.friend_request_num);
//
//        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        noFriendLayout = view.findViewById(R.id.no_friend_layout);
//
//        Contacts = new ArrayList<>();
//        requests = new ArrayList<>();
//
//        getRequests();
//        readContacts();
//
//        contactAdapter = new ContactAdapter(getContext(), Contacts);
//        recyclerView.setAdapter(contactAdapter);
//
//        return view;
//    }
//
//    private void getRequests() {
//        FirebaseDatabase.getInstance().getReference("Requests").child(uid)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        requests.clear();
//                        if (snapshot.getValue() != null) {
//                            long count = snapshot.getChildrenCount();
//                            friend_request_num.setVisibility(View.VISIBLE);
//                            friend_request_num.setText(String.valueOf(count));
//                            for (DataSnapshot child : snapshot.getChildren()) {
//                                requests.add(child.getValue(User.class));
//                            }
//                        } else {
//                            friend_request_num.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//                });
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    private void searchContact(String s) {
//        for (int i = Contacts.size() - 1; i >= 0; i--) {
//            String username = Contacts.get(i).getUsername();
//            if (!username.contains(s)) {
//                Contacts.remove(i);
//            }
//        }
//        contactAdapter.notifyDataSetChanged();
//
//    }
//
//    private void readContacts() {
//        FirebaseDatabase.getInstance().getReference("Friends").child(uid).orderByChild("username").
//                addValueEventListener(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    noFriendLayout.setVisibility(View.GONE);
//                }else {
//                    noFriendLayout.setVisibility(View.VISIBLE);
//                }
//                Contacts.clear();
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    User user = child.getValue(User.class);
//                    Contacts.add(user);
//                }
//                contactAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    public void onAddFriendClick() {
//        Intent intent = new Intent(getActivity(), AddFriendActivity.class);
//        startActivity(intent);
//    }
//
//}




package com.example.UNISEA.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.UNISEA.Adapter.ContactAdapter;
import com.example.UNISEA.AddFriendActivity;
import com.example.UNISEA.FriendRequestActivity;
import com.example.UNISEA.Model.Contact;
import com.example.UNISEA.Model.User;
import com.example.UNISEA.R;
import com.example.UNISEA.RegisterActivity;
import com.example.UNISEA.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {
    private ImageView addFriend;
    private androidx.appcompat.widget.SearchView searchView;
    private TextView friend_request;
    private TextView friend_request_num;
    private LinearLayout noFriendLayout;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<User> Contacts;
    private String uid;
    private ArrayList<User> requests;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
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
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        uid = FirebaseAuth.getInstance().getUid();

        // Bind the addFriend image with OnClick
        addFriend = view.findViewById(R.id.AddFriend);
        addFriend.setOnClickListener(v -> onAddFriendClick());

        // TODO: complete the search listening algorithm
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchContact(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    readContacts();
                } else {
                    searchContact(newText);
                }
                return false;
            }
        });

        friend_request = view.findViewById(R.id.friend_request);
        friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), FriendRequestActivity.class);
                intent.putExtra("requests",requests);
                startActivity(intent);
            }
        });

        friend_request_num = view.findViewById(R.id.friend_request_num);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noFriendLayout = view.findViewById(R.id.no_friend_layout);

        Contacts = new ArrayList<>();
        requests = new ArrayList<>();

        getRequests();
        readContacts();

        contactAdapter = new ContactAdapter(getContext(), Contacts);
        recyclerView.setAdapter(contactAdapter);

        return view;
    }

    private void getRequests() {
        Log.d("UIDDDDD",uid.toString());
        FirebaseDatabase.getInstance().getReference("Requests").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requests.clear();
                        if (snapshot.getValue() != null) {
                            long count = snapshot.getChildrenCount();
                            friend_request_num.setVisibility(View.VISIBLE);
                            friend_request_num.setText(String.valueOf(count));
                            for (DataSnapshot child : snapshot.getChildren()) {
                                requests.add(child.getValue(User.class));
                            }
                        } else {
                            friend_request_num.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchContact(String s) {
        for (int i = Contacts.size() - 1; i >= 0; i--) {
            String username = Contacts.get(i).getUsername();
            if (!username.contains(s)) {
                Contacts.remove(i);
            }
        }
        contactAdapter.notifyDataSetChanged();

    }

    private void readContacts() {
        FirebaseDatabase.getInstance().getReference("Friends").child(uid).orderByChild("username").
                addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            noFriendLayout.setVisibility(View.GONE);
                        }else {
                            noFriendLayout.setVisibility(View.VISIBLE);
                        }
                        Contacts.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            Contacts.add(user);
                        }
                        contactAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void onAddFriendClick() {
        Intent intent = new Intent(getActivity(), AddFriendActivity.class);
        startActivity(intent);
    }

}