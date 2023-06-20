package com.example.UNISEA;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // skip login for testing
//        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            Toast.makeText(this,"Welcome!" + mUser.getEmail(),Toast.LENGTH_LONG).show();
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        }


        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, LoginActivity.class)));

        TextView registerText = findViewById(R.id.SignUp);
        registerText.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class)));

//        Button uiLoginButton = findViewById(R.id.UILoginBUtton);
//        uiLoginButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, UILoginActivity.class)));

    }

}