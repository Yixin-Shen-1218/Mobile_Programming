package com.example.UNISEA;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.UNISEA.Fragment.ChatsFragment;
import com.example.UNISEA.Fragment.ContactsFragment;
import com.example.UNISEA.Fragment.DiscoverFragment;
import com.example.UNISEA.Fragment.MatchFragment;
import com.example.UNISEA.Fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    BottomNavigationView navView;

    // Click listener for choosing different navigation tabs
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                // To show Chats area
                case R.id.navigation_chats: {
                    Fragment chatsFragment = ChatsFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_navbar, chatsFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                // To show Contacts area
                case R.id.navigation_contacts: {
                    Fragment contactsFragment = ContactsFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_navbar, contactsFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                // To show Discover area
                case R.id.navigation_discover: {
                    Fragment discoverFragment = DiscoverFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_navbar, discoverFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                // To show Match area
                case R.id.navigation_match: {
                    Fragment matchFragment = MatchFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_navbar, matchFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                // To show Profile area
                case R.id.navigation_profile: {
                    Fragment profileFragment = ProfileFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_navbar, profileFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting for Navigation Bar
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setItemIconTintList(null);

        // Setting for Fragments
        Fragment chatsFragment = ChatsFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_navbar, chatsFragment)
                .addToBackStack(null)
                .commit();

        ButterKnife.bind(this);

        // user login
//        auth = FirebaseAuth.getInstance();
//
//        auth.signInWithEmailAndPassword("yixin@123.com", String.valueOf(123456));
//
//        Log.d("Main", String.valueOf(auth.getUid()));
//
//        auth.signOut();

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("Main", String.valueOf(mUser));

//        auth.signOut();

//        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
//        startActivity(intent);

//        if (mUser == null) {
//            startActivity(new Intent(MainActivity.this, LoadingActivity.class));
//        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        switch (id) {
            case 1: {
                navView.getMenu().getItem(0).setChecked(true);
                Fragment chatsFragment = ChatsFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.layout_navbar, chatsFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case 2: {
                navView.getMenu().getItem(1).setChecked(true);
                Fragment contactsFragment = ContactsFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.layout_navbar, contactsFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case 3: {
                navView.getMenu().getItem(2).setChecked(true);
                Fragment matchFragment = MatchFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.layout_navbar, matchFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case 4: {
                navView.getMenu().getItem(3).setChecked(true);
                Fragment discoverFragment = DiscoverFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.layout_navbar, discoverFragment)
                        .addToBackStack(null)
                        .commit();

                break;
            }
            case 5: {
                navView.getMenu().getItem(4).setChecked(true);
                Fragment profileFragment = ProfileFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.layout_navbar, profileFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
    }
}