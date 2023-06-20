package com.example.UNISEA;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.UNISEA.Model.Blocked;
import com.example.UNISEA.Model.Contact;
import com.example.UNISEA.Model.Profile;
import com.example.UNISEA.Model.User;
import com.example.UNISEA.Util.LocationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register";
    private FirebaseAuth mAuth;
    private LocationHelper locationHelper;

    private ProgressBar progressBar;
    private Button registerButton;
    private ImageView backButton;
    private TextInputEditText username;
    private TextInputEditText email;
    private TextInputEditText password;

    private final int Request_Code_Location = 22;

    String[] uni_items = {"University of Melbourne", "University of Sydney",
            "University of Queensland", "University of Monash", "University of New South Wales",
            "Australian National University", "University of Adelaide", "University of Western Australia"};
    private AutoCompleteTextView uniMenu;
    private ArrayAdapter<String> adapterUniItems;

    String[] gender_items = {"Male", "Female", "Non-binary"};
    private AutoCompleteTextView genderMenu;
    private ArrayAdapter<String> adapterGenderItems;

    private final String defaultUrl = "https://firebasestorage.googleapis.com/v0/b/assignment2-daac3.appspot.com/o/default.jpg?alt=media&token=b929ad92-8e2d-499a-8a80-47429f16b40e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.editTextUsernameInput);
        email = findViewById(R.id.editTextEmailInput);
        password = findViewById(R.id.editTextPasswordInput);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // Click and return to WelcomeActivity
        backButton = findViewById(R.id.BackBtn);
        backButton.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, WelcomeActivity.class)));


        // TODO: Add the university information to the Users table
        uniMenu = findViewById(R.id.UniversityMenuList);
        adapterUniItems = new ArrayAdapter<String>(this, R.layout.list_item, uni_items);
        uniMenu.setAdapter(adapterUniItems);
        uniMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(getApplicationContext(), "item: " + item, Toast.LENGTH_SHORT).show();
            }
        });


        // TODO: Add the Gender information to the Users table
        genderMenu = findViewById(R.id.GenderMenuList);
        adapterGenderItems = new ArrayAdapter<String>(this, R.layout.list_item, gender_items);
        genderMenu.setAdapter(adapterGenderItems);
        genderMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(getApplicationContext(), "item: " + item, Toast.LENGTH_SHORT).show();
            }
        });


        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> register());

        locationHelper = new LocationHelper(getApplicationContext(), RegisterActivity.this);
        locationHelper.updateLocation();
    }

    private void register() {
        String username = this.username.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String gender = this.genderMenu.getText().toString();
        String university = this.uniMenu.getText().toString();

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
        if (username.isEmpty()) {
            this.username.setError("Username required!");
            this.username.requestFocus();
            return;
        }
        if (university.isEmpty()) {
            this.uniMenu.setError("University required!");
            this.uniMenu.requestFocus();
            return;
        }
        if (gender.isEmpty()) {
            this.genderMenu.setError("Gender required!");
            this.genderMenu.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "register: start");
                    if (task.isSuccessful()) {
                        String uid = mAuth.getUid();

                        // store user
                        User user = new User(uid, username, defaultUrl, 0);
                        FirebaseDatabase.getInstance().getReference("new_Users").child(uid).setValue(user);

                        // store user profile
                        Profile profile = new Profile();
                        profile.setUniversity(university);
                        profile.setGender(gender);
                        FirebaseDatabase.getInstance().getReference("Profile").child(uid).setValue(profile);



                        Blocked new_block = new Blocked(uid);
                        FirebaseDatabase.getInstance().getReference("BlockedMatch").child(uid).child(uid).setValue(new_block);

                        Contact new_contact = new Contact(uid, defaultUrl, username);
                        FirebaseDatabase.getInstance().getReference("Contacts").child(uid).child(uid).setValue(new_contact);

                        locationHelper.setUserLocation();
                        Toast.makeText(RegisterActivity.this,"Registration successful!",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Registration fail, please try again!",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
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