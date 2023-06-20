package com.example.UNISEA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.UNISEA.Model.LocationPos;
import com.example.UNISEA.Util.LocationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText email;
    private TextInputEditText password;
    private ImageView backButton;
    private Button loginButton;
    private Button thirdPartyButton;
    private ProgressBar progressBar;

    private final int Request_Code_Location = 22;

    private FirebaseAuth mAuth;
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        email = findViewById(R.id.editTextEmailInput);
        password = findViewById(R.id.editTextPasswordInput);
        backButton = findViewById(R.id.BackBtn);
        loginButton = findViewById(R.id.loginButton);
        thirdPartyButton = findViewById(R.id.thirdPartyButton);

        backButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, WelcomeActivity.class)));
        loginButton.setOnClickListener(v -> login());
        thirdPartyButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, UILoginActivity.class)));

//        setLocationAPI();
        locationHelper = new LocationHelper(getApplicationContext(), LoginActivity.this);
        locationHelper.updateLocation();
    }

    private void login() {
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        if (email.isEmpty()) {
            this.email.setError("Email required!");
            this.email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Email format not valid!");
            this.email.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            this.password.setError("Password required!");
            this.password.requestFocus();
            return;
        }
        if(password.length() < 6) {
            this.password.setError("Password must longer than 6 digits!");
            this.password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // redirect to profile page
                    locationHelper.setUserLocation();
                    Toast.makeText(LoginActivity.this,"Login successful!",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this,"Login fail, please try again!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("test", "request permission");
        if(requestCode == Request_Code_Location){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationHelper.updateLocation();
            }
        }
    }

}