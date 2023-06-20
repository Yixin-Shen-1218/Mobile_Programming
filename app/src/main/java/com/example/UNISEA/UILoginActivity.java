package com.example.UNISEA;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class UILoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private List<AuthUI.IdpConfig> providers;
    private ImageView backButton;
    private RecyclerView thirdPartyList;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener != null) {
            mAuth.removeAuthStateListener(listener);
        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uilogin);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        // back to the sign in page
        backButton = findViewById(R.id.BackBtn);
        backButton.setOnClickListener(v -> startActivity(new Intent(UILoginActivity.this, LoginActivity.class)));

        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        thirdPartyList = findViewById(R.id.ThirdPartyList);




        init();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    Toast.makeText(getApplicationContext(),"Welcome!" + user.getEmail(),Toast.LENGTH_LONG).show();
                    startActivity(new Intent(UILoginActivity.this, MainActivity.class));
                }
//                else {
//
                    startActivity(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build());
////                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),AUTH_CODE);
//                }
            }
        };
    }

}