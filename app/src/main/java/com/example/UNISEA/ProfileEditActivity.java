package com.example.UNISEA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.UNISEA.Model.Profile;
import com.example.UNISEA.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class ProfileEditActivity extends AppCompatActivity {
    private Calendar cldr;
    private RelativeLayout matchPreference;
    private EditText username_text;
    private TextView DOB_text;
    private TextView Gender_text;
    private TextView University_text;
    private EditText Height_text;
    private EditText About_text;
    private EditText Tag1_text;
    private EditText Tag2_text;
    private EditText Tag3_text;
    private EditText Tag4_text;
    private TextView SAVE_TEXT;

    private RoundedImageView avatar;
    private TextView editAvatar;
    private ProgressBar progressBar;

    private String uid;
    private User user;
    private Profile profile;

    private Uri imageUri;

    private final static int AVATAR_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        this.uid = FirebaseAuth.getInstance().getUid();

        progressBar = findViewById(R.id.save_profile);
        progressBar.setVisibility(View.GONE);

        avatar = findViewById(R.id.avatar);
        editAvatar = findViewById(R.id.avatar_edit);
        editAvatar.setOnClickListener(v -> checkPermission());

        cldr = Calendar.getInstance();
        cldr.setTimeZone(TimeZone.getTimeZone("GMT+11"));

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> onBackPressed());

        matchPreference = findViewById(R.id.match_edit);
        matchPreference.setOnClickListener(view -> onMatchEditPressed());

        username_text = findViewById(R.id.username_text);

        DOB_text = findViewById(R.id.DOB_text);
        DOB_text.setOnClickListener(v -> pickDOB());

        Gender_text = findViewById(R.id.Gender_text);
        Gender_text.setOnClickListener(v -> pickGender());

        University_text = findViewById(R.id.University_text);
        University_text.setOnClickListener(v -> pickUniversity());

        Height_text = findViewById(R.id.Height_text);
        Height_text.setOnClickListener(v -> pickHeight());

        About_text = findViewById(R.id.About_text);

        Tag1_text = findViewById(R.id.Tag1_text);
        Tag2_text = findViewById(R.id.Tag2_text);
        Tag3_text = findViewById(R.id.Tag3_text);
        Tag4_text = findViewById(R.id.Tag4_text);


        SAVE_TEXT = findViewById(R.id.SAVE_TEXT);
        SAVE_TEXT.setOnClickListener(v -> SaveUpdate());

        loadUser();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) { // permission not granted
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permissions,AVATAR_CODE);
        } else { // permission granted
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,AVATAR_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AVATAR_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(getApplicationContext(),"You just denied the permission!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == AVATAR_CODE) {
            imageUri = data.getData();
            avatar.setImageURI(imageUri);
        }
    }

    private void pickDOB() {
        int year = cldr.get(Calendar.YEAR);
        int month = cldr.get(Calendar.MONTH);
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("ResourceType") DatePickerDialog picker = new DatePickerDialog(ProfileEditActivity.this, 3,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fMonth = String.format(Locale.getDefault(),"%02d",month + 1);
                        String fDayOfMonth = String.format(Locale.getDefault(),"%02d",dayOfMonth);
                        DOB_text.setText(fDayOfMonth + "/" + fMonth + "/" + year);
                    }
                },year,month,day);
        picker.show();
    }


    private void pickGender() {
        CharSequence[] gender = new CharSequence[]{"Male", "Female", "Non-Binary"};
        AlertDialog dialog = new AlertDialog.Builder(ProfileEditActivity.this,3)
                .setItems(gender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Gender_text.setText(gender[which].toString());
                    }
                })
                .create();

        dialog.show();
    }

    private void pickUniversity() {
        CharSequence[] university = new CharSequence[]{"University of Melbourne", "University of Sydney",
                "University of Queensland", "University of Monash", "University of New South Wales",
                "Australian National University", "University of Adelaide", "University of Western Australia"};
        AlertDialog dialog = new AlertDialog.Builder(ProfileEditActivity.this,3)
                .setItems(university, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        University_text.setText(university[which].toString());
                    }
                })
                .create();

        dialog.show();
    }

    private void pickHeight() {

    }

    private void loadUser() {
        FirebaseDatabase.getInstance().getReference("new_Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                        loadProfile();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadProfile() {
        FirebaseDatabase.getInstance().getReference("Profile").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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

        String birth = profile.getBirth();
        int height = profile.getHeight();
        String university = profile.getUniversity();
        String gender = profile.getGender();
        String about = profile.getAbout();
        String tag1 = profile.getTag1();
        String tag2 = profile.getTag2();
        String tag3 = profile.getTag3();
        String tag4 = profile.getTag4();

        Glide.with(ProfileEditActivity.this).load(url).into(avatar);

        if (!username.equals("")) {
            username_text.setText(username);
        }
        if (!birth.equals("")) {
            DOB_text.setText(birth);
        }
        if (height != 0) {
            Height_text.setText(String.valueOf(height));
        }
        if (!university.equals("")) {
            University_text.setText(university);
        }
        if (!gender.equals("")) {
            Gender_text.setText(gender);
        }
        if (!about.equals("")) {
            About_text.setText(about);
        }
        if (!tag1.equals(""))  {
            Tag1_text.setText(tag1);
        }
        if (!tag2.equals(""))  {
            Tag2_text.setText(tag2);
        }
        if (!tag3.equals(""))  {
            Tag3_text.setText(tag3);
        }
        if (!tag4.equals(""))  {
            Tag4_text.setText(tag4);
        }
    }

    private void SaveUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);
        builder.setTitle("Save profile：");
        builder.setMessage("Do you really want to save your profile");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                updateUser();
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

    private void updateUser() {

        user.setUsername(username_text.getText().toString());

        if (imageUri == null) {
            saveUser();
        } else {
            String randomKey = UUID.randomUUID().toString();
            String avatarPath = "avatars/" + randomKey;
            StorageReference avatarRef = FirebaseStorage.getInstance().getReference().child(avatarPath);
            avatarRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return avatarRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        user.setUrl(task.getResult().toString());
                        saveUser();
                    }
                }
            });
        }





    }

    private void saveUser() {
        FirebaseDatabase.getInstance().getReference("new_Users").child(uid)
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        saveProfile();
                    }
                });
    }


    private void saveProfile() {
        profile.setBirth(DOB_text.getText().toString());
        profile.setGender(Gender_text.getText().toString());
        profile.setUniversity(University_text.getText().toString());
        try {
            profile.setHeight(Integer.parseInt(Height_text.getText().toString()));
        } catch (NumberFormatException ignored) {}
        profile.setAbout(About_text.getText().toString());
        profile.setTag1(Tag1_text.getText().toString());
        profile.setTag2(Tag2_text.getText().toString());
        profile.setTag3(Tag3_text.getText().toString());
        profile.setTag4(Tag4_text.getText().toString());

        FirebaseDatabase.getInstance().getReference("Profile").child(uid)
                .setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Update success!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Log.d("icon_test", "onAddFriendClick ");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("return", "Hello from the second activity.");
        setResult(RESULT_OK, returnIntent);

        super.onBackPressed();
    }

    private void onMatchEditPressed() {
        Intent intent = new Intent(ProfileEditActivity.this, EditMatchProfileActivity.class);
        startActivity(intent);
    }
}