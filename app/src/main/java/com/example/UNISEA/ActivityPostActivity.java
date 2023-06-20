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
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.UNISEA.Model.ActivityDetail;
import com.example.UNISEA.Model.Post;
import com.example.UNISEA.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class ActivityPostActivity extends AppCompatActivity {

    private Calendar cldr;
    private User user;
    private ProgressDialog dialog;

    private Uri storageUri;
    private StorageReference storageRef;
    private String imagePath;
    private Bitmap imageBitmap;

    private EditText activity;
    private TextView dateView;
    private TextView startTimeView;
    private TextView endTimeView;
    private TextView visibilityView;
    private TextView locationView;
    private ImageView imageView;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int TAKE_PHOTO_CODE = 1001;
    private static final int CAMERA_CODE = 1002;
    private static final int GALLERY_CODE = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_post);

        cldr = Calendar.getInstance();
        cldr.setTimeZone(TimeZone.getTimeZone("GMT+11"));
        storageRef = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading activity...");

        activity = findViewById(R.id.textView_post_activity);
        dateView = findViewById(R.id.textView_date);
        startTimeView = findViewById(R.id.textView_start_time);
        endTimeView = findViewById(R.id.textView_end_time);
        visibilityView = findViewById(R.id.textView_visibility);
        locationView = findViewById(R.id.textView_location);
        imageView = findViewById(R.id.image_post_activity);

        Button postButton = findViewById(R.id.button_post_activity);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = FirebaseAuth.getInstance().getUid();
                getInfo(id);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] sequences = new CharSequence[]{"Camera", "Gallery", "Cancel"};
                AlertDialog dialog = new AlertDialog.Builder(ActivityPostActivity.this)
                        .setTitle("Select image")
                        .setItems(sequences, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: {
                                        checkCameraPermission();
                                        break;
                                    }
                                    case 1: {
                                        checkGalleryPermission();
                                        break;
                                    }
                                    case 2: {
                                        dialog.cancel();
                                        break;
                                    }
                                }
                            }
                        }).create();
                dialog.show();
            }
        });

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        startTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickStartTime();
            }
        });

        endTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEndTime();
            }
        });

        visibilityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickVisibility();
            }
        });

        locationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickLocation();
            }
        });

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) { // permission not granted
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permissions,GALLERY_CODE);
        } else { // permission granted
            pickImage();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this,permissions,CAMERA_CODE);
        } else {
            takePhoto();
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,TAKE_PHOTO_CODE);
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(getApplicationContext(),"You just denied the permission!", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(getApplicationContext(),"You just denied the permission!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK_CODE: {
                    try {
                        Uri imageUri = data.getData();
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        imageView.setImageBitmap(imageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case TAKE_PHOTO_CODE: {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(imageBitmap);
                    break;
                }
            }
        }
    }

    private void uploadImage() {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
        byte[] bytes = os.toByteArray();

        String randomKey = UUID.randomUUID().toString();
        imagePath = "images/" + randomKey;
        StorageReference imagesRef = storageRef.child(imagePath);
        UploadTask uploadTask = imagesRef.putBytes(bytes);

        // get image url in firebase storage
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return imagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    storageUri = task.getResult();
                    postActivity();
                }
            }
        });

    }

    private void pickDate() {
        int year = cldr.get(Calendar.YEAR);
        int month = cldr.get(Calendar.MONTH);
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("ResourceType") DatePickerDialog picker = new DatePickerDialog(ActivityPostActivity.this, 3,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fMonth = String.format(Locale.getDefault(),"%02d",month + 1);
                        String fDayOfMonth = String.format(Locale.getDefault(),"%02d",dayOfMonth);
                        dateView.setText(year + "/" + fMonth + "/" + fDayOfMonth);
                    }
                },year,month,day);
        picker.show();
    }

    private void pickStartTime() {
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        TimePickerDialog picker = new TimePickerDialog(ActivityPostActivity.this, 3,
                new  TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        if (validTime(true,sHour,sMinute)) {
                            String fHour = String.format(Locale.getDefault(),"%02d",sHour);
                            String fMinute = String.format(Locale.getDefault(),"%02d",sMinute);
                            startTimeView.setText(fHour + ":" + fMinute);
                        } else {
                            Toast.makeText(getApplicationContext(),"Start time must before end time, " +
                                    "please try again!",Toast.LENGTH_LONG).show();
                        }
                    }
                }, hour, minutes, true);
        picker.show();
    }

    private void pickEndTime() {
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        TimePickerDialog picker = new TimePickerDialog(ActivityPostActivity.this, 3,
                new  TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        if (validTime(false,sHour,sMinute)) {
                            String fHour = String.format(Locale.getDefault(),"%02d",sHour);
                            String fMinute = String.format(Locale.getDefault(),"%02d",sMinute);
                            endTimeView.setText(fHour + ":" + fMinute);
                        } else {
                            Toast.makeText(getApplicationContext(),"End time must after start time, " +
                                    "please try again!",Toast.LENGTH_LONG).show();
                        }
                    }
                }, hour, minutes, true);
        picker.show();
    }

    // start time must before end time
    private boolean validTime(boolean isStartTime, int hour, int minutes) {
        if (isStartTime) {
            String endTime = endTimeView.getText().toString();
            if (endTime.equals("End time")) { // end time not selected
                return true;
            } else {
                String[] end = endTime.split(":");
                int endHour = Integer.parseInt(end[0]);
                int endMinute = Integer.parseInt(end[1]);
                return hour < endHour || (hour == endHour && minutes < endMinute);
            }
        } else {
            String startTime = startTimeView.getText().toString();
            if (startTime.equals("Start time")) { // start time not selected
                return true;
            } else {
                String[] start = startTime.split(":");
                int startHour = Integer.parseInt(start[0]);
                int startMinutes = Integer.parseInt(start[1]);
                return startHour < hour || (startHour == hour && startMinutes < minutes);
            }
        }
    }

    private void pickVisibility() {
        CharSequence[] post = new CharSequence[]{"Public", "Private"};
        AlertDialog dialog = new AlertDialog.Builder(ActivityPostActivity.this,3)
                .setItems(post, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        visibilityView.setText(post[which].toString());
                    }
                })
                .create();

        dialog.show();
    }

    private void pickLocation() {
        CharSequence[] post = new CharSequence[]{"University of Melbourne", "Melbourne Central",
                "Docklands", "Queen Victoria Market"};
        AlertDialog dialog = new AlertDialog.Builder(ActivityPostActivity.this,3)
                .setItems(post, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationView.setText(post[which].toString());
                    }
                })
                .create();

        dialog.show();
    }

    private void getInfo(String id) {
        FirebaseDatabase.getInstance().getReference("new_Users")
                .child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        user = task.getResult().getValue(User.class);
                        dialog.show();
                        if (imageBitmap != null) {
                            uploadImage();
                        } else {
                            postActivity();
                        }
                    }
                });
    }


    private void postActivity() {
        String date = dateView.getText().toString();
        String startTime = startTimeView.getText().toString();
        String endTime = endTimeView.getText().toString();
        String location =  locationView.getText().toString();
        ActivityDetail detail = new ActivityDetail(date,startTime,endTime,location);

        boolean visible = visibilityView.getText().toString().equals("Public");
        String content = activity.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+11"));
        String time = sdf.format(new Date());

        long order = -System.currentTimeMillis();

        String uid = user.getUid();
        int count = user.getPost_count() + 1;
        String pid = uid + "_" + String.format(Locale.getDefault(),"%03d",count);
        String image;

        if (storageUri == null) {
            image = "";
        } else {
            image = storageUri.toString();
        }

        String username = user.getUsername();
        String avatar = user.getUrl();

        Post post =  new Post(content, time, order, uid, pid, image, username, avatar, detail, true, visible);

        // update post_count
        FirebaseDatabase.getInstance().getReference("new_Users").child(uid).child("post_count")
                .setValue(count);

        // write post
        FirebaseDatabase.getInstance().getReference("Posts").child(pid).setValue(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"post success!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }


    @Override
    public void onBackPressed() {

        // delete uploaded image
        if (imagePath != null) {
            storageRef.child(imagePath).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("return", "Hello from the second activity.");
                    setResult(RESULT_OK, returnIntent);
                }
            });
        }

        super.onBackPressed();
    }
}