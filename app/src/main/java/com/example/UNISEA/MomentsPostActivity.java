package com.example.UNISEA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class MomentsPostActivity extends AppCompatActivity {

    private TextView moment;
    private User user;
    private ImageView image;
    private ProgressDialog dialog;

    private Uri storageUri;
    private StorageReference storageRef;
    private String imagePath;
    private Bitmap imageBitmap;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int TAKE_PHOTO_CODE = 1001;
    private static final int CAMERA_CODE = 1002;
    private static final int GALLERY_CODE = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments_post);

        storageRef = FirebaseStorage.getInstance().getReference();
        moment = findViewById(R.id.textView_post_activity);
        String id = FirebaseAuth.getInstance().getUid();
        dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading moments...");

        Button postButton = findViewById(R.id.button_post_activity);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo(id);
            }
        });

        image = findViewById(R.id.image_post_activity);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] sequences = new CharSequence[]{"Camera", "Gallery", "Cancel"};
                AlertDialog dialog = new AlertDialog.Builder(MomentsPostActivity.this)
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
                        image.setImageBitmap(imageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case TAKE_PHOTO_CODE: {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    image.setImageBitmap(imageBitmap);
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
                    post();
                }
            }
        });
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
                            post();
                        }
                    }
                });
    }

    private void post() {
        String content = moment.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+11"));
        String time = sdf.format(new Date());

        long order = -System.currentTimeMillis();
        String uid = user.getUid();
        int count = user.getPost_count() + 1;
        String pid = uid + "_" + String.format(Locale.getDefault(), "%03d", count);

        String image;
        if (storageUri == null) {
            image = "";
        } else {
            image = storageUri.toString();
        }

        String username = user.getUsername();
        String avatar = user.getUrl();

        // moment has no detail
        ActivityDetail detail = new ActivityDetail();
        detail.emptyDetail();

        // write post
        Post post = new Post(content, time, order, uid, pid, image, username, avatar, detail, false, false);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Posts");
        updateCount(uid, count);
        database.child(pid).setValue(post);

        dialog.dismiss();
        Toast.makeText(getApplicationContext(), "Post succeed!", Toast.LENGTH_LONG).show();

        finish();
    }

    private void updateCount(String id, int count) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("new_Users");
        database.child(id).child("post_count").setValue(count);
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