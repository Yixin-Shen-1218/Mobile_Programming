package com.example.UNISEA;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {
    private int recLen = 5; //set the time limit
    private TextView tv;
    Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);
        initView();
        timer.schedule(task, 1000, 1000);

        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                if (mUser == null) {
                    startActivity(new Intent(LoadingActivity.this, WelcomeActivity.class));
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                }else{
                    startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                }
                finish();
            }
        }, 5000); // set the time limit
    }
    private void initView() {
        tv = findViewById(R.id.tv);
        tv.setOnClickListener(v -> Jump_to());
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    recLen--;
                    tv.setText("Skip " + recLen);
                    if (recLen < 0) {
                        timer.cancel();
                        tv.setVisibility(View.GONE);
                    }
                }
            });
        }
    };


    public void Jump_to() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser == null) {
            startActivity(new Intent(LoadingActivity.this, WelcomeActivity.class));
        }else{
            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
        }
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }

        finish();
    }
}