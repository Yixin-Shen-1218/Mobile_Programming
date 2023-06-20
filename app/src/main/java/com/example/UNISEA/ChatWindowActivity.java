package com.example.UNISEA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.widget.Toast;
import android.os.Environment;
import android.os.StrictMode;


import com.example.UNISEA.Adapter.ChatAdapter;
import com.example.UNISEA.Adapter.ChatRecordsAdapter;
import com.example.UNISEA.Fragment.ChatsFragment;
import com.example.UNISEA.Model.Chat;
import com.example.UNISEA.Model.ChatRecord;
import com.example.UNISEA.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ChatWindowActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView Username;
    private RecyclerView recyclerView;
    private List<String> historyChatRecords;
    private ChatRecordsAdapter chatRecordsAdapter;
    private TextInputEditText inputEditText;
    private ImageView send;
    private DatabaseReference myRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private DatabaseReference msgQueRef;
    private String uid;  //the uid of the current user
    private String uidChatWith;
    private ValueEventListener listener;
    private DatabaseReference msgQueRef;
    private String filename;
    private String avatar;
    private String username;
    private String avatar_mine;
    private final int TAKE_PHOTO = 2;
    private final int FROM_GALLERY = 3;
    private Uri uri;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

//        //for test sensor
//        //for test
//        SensorManager sensorManager;
//        Sensor sensor;
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        // sensor might be null if the device not support TYPE_STEP_COUNTER
//        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
////        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//
//        SensorEventListener listener = new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent sensorEvent) {
//                Log.d("Sensor", Arrays.toString(sensorEvent.values));
////                float steps = sensorEvent.values()[0];
////                Log.d("STEPS",steps);
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int i) {
//
//            }
//        };
//        if(sensor==null)Log.d("Sensor","NULL");
//        if(listener==null)Log.d("Listener","NULL");
//        sensorManager.registerListener(listener, sensor,0);



        // use local db to test
        // TODO: replace to online db in final version
//        database.useEmulator("10.0.2.2", 9000);
        myRef = database.getReference();

       /////

        // Click and return to WelcomeActivity
        backButton = findViewById(R.id.BackBtn);
        backButton.setOnClickListener(v -> onBackPressed());

//        // Get the UID of the chatting friend from ChatsFragment
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        avatar = intent.getStringExtra("Avatar");
        uid = FirebaseAuth.getInstance().getUid();
        uidChatWith = intent.getStringExtra("UID");
        // the avatar of current user.

//         TODO: remove test in final version
//        uid = "3vheCH6jreaIhfgRuSlHTQvXc032";
//        uidChatWith = "DC3kduN9RWOkeYnqAt5a3CKbiRB3";

//        uid = "DC3kduN9RWOkeYnqAt5a3CKbiRB3";
//        uidChatWith = "3vheCH6jreaIhfgRuSlHTQvXc032";


        Log.d("ChatWindowActivity", uidChatWith);
        Log.d("ChatWindowActivity", username);
        setMyAvatar(uid);

        // the chat histroy with uidChatWith, for every friend, there is a record history
        filename = "ChatHitory" + "_" + uidChatWith + "_.txt";

        Username = findViewById(R.id.Username);
        Username.setText(username);

        recyclerView = findViewById(R.id.recycler_view_chat_window);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        // Resize recycler view when keyboard appears
        ((LinearLayoutManager)recyclerView.getLayoutManager()).setStackFromEnd(true);

        historyChatRecords = new ArrayList<>();
//        readHistoryRecords();

        ArrayList<ChatRecord> localHistory = new ArrayList<>();
        chatRecordsAdapter = new ChatRecordsAdapter(getApplicationContext(), localHistory);


//        // load the local history when enter the chat window
        saveRecords(filename);
//        ArrayList<ChatRecord> localHistory = readFromFile(filename);
//        chatRecordsAdapter = new ChatRecordsAdapter(this.getApplicationContext(), localHistory);
//        recyclerView.setAdapter(chatRecordsAdapter);


        inputEditText = findViewById(R.id.EnterTextInput);

        send = findViewById(R.id.Send);
        // listener for inputTextFiled, change the icon of send button from add.png to send.png
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(inputEditText.getText().toString().equals("")){
                    send.setImageResource(R.drawable.add);
                }
                else send.setImageResource(R.drawable.send);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputEditText.getText()==null || inputEditText.getText().toString().equals("")){
                    selectImg();
                }
                else SendMessage(uid,uidChatWith,inputEditText.getText().toString(),"0");
            }
        });
        receiveMsg(uid, uidChatWith);

    }

    // TODO: Send the message
    // user1 send the msg to database and append it to the unread queue of user2
    private void SendMessage(String UID1, String UID2, String msg, String type) {
        Log.d("MSG is!!",msg);
        if(msg==""||msg==null)return;
        String msg_ = type + " " + msg;
        DatabaseReference msgQueRef = myRef.child("Msg").child(UID2).child("UnreadMsg").child(UID1).child("MsgQueue");
        msgQueRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    ArrayList<String> msgQueue = (ArrayList<String>)task.getResult().getValue();
                    if(msgQueue==null){
                        msgQueue = new ArrayList<String>();
                    }
                    Log.d("MSG is!!",msg);
                    // update the online db
                    // use a num to specify msg type
                    msgQueue.add(msg_);
                    msgQueRef.setValue(msgQueue);
                }
            }
        });
        Log.d("ChatWindow", String.valueOf(inputEditText.getText()));
        ChatRecord record;
        String history;
        if(type.equals("0")){
            Log.d("MMMSG",msg);
            record = new ChatRecord(uid, avatar_mine, 1, msg);
            history = uid + " 1 " + msg_;
        }
        else{
            record = new ChatRecord(uid, avatar_mine, 3, msg);
            history = uid + " 3 " +msg_;
        }
        // add to local history

        historyChatRecords.add(history);
        this.chatRecordsAdapter.updateRecords(record);
        recyclerView.setAdapter(chatRecordsAdapter);
        this.inputEditText.setText("");
    }

    //receive msg while chatting
    private void receiveMsg(String UID, String uidChatWith){
        Log.d("STILLLL","OUTTTT");
        msgQueRef = myRef.child("Msg").child(UID).child("UnreadMsg").child(uidChatWith).child("MsgQueue");
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    String type = snapshot.getValue().getClass().toString();
                    Log.d("msg", type);
                    ArrayList<String> newMsgs = (ArrayList<String>) snapshot.getValue();
                    String msg;
                    for(String item:newMsgs){
                        msg = item;
                        String[] msg_ = msg.split(" ");
                        ChatRecord record;
                        String history;
                        Log.d("PPP",msg);
                        Log.d("MSGGGGGG",msg_[1]);
                        if(msg_[0].equals("0")){
                            record = new ChatRecord(uidChatWith,avatar ,0, msg_[1]);
                            history = uidChatWith+ " 0 " + msg;
                        }
                        else{
                            record = new ChatRecord(uidChatWith,avatar ,2, msg_[1]);
                            history = uidChatWith+ " 2 " + msg;
                        }

                        chatRecordsAdapter.updateRecords(record);

                        // a simple method to segment the record where the msg should not
                        //maintain space " "
                        historyChatRecords.add(history);
                    }
//                    recyclerView.setAdapter(chatRecordsAdapter);
                    //if(getLifecycle())
                    msgQueRef.setValue(new ArrayList<String>());

                    String ste = getLifecycle().getCurrentState().toString();
                    Log.d("STATE",ste);
                    Log.d("STILLLL","receive");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        msgQueRef.addValueEventListener(listener);
    }

    private void sendImage(String UID1, String UID2, String URL){
    }




    // Return to the ChatsFragment
    @Override
    public void onBackPressed() {
        Log.d("ChatWindowActivity", "Return to the chat activity ");
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
        msgQueRef.removeEventListener(listener);
        saveRecords(filename);

    }

    @Override
    public void onStart(){
        super.onStart();

    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("Paused","Paused");
    }

    @Override
    public void onResume(){
        super.onResume();
//        msgQueRef.addValueEventListener(listener);
        //String ste = getLifecycle().getCurrentState().toString();
    }

    private void saveRecords(String filename){

        String contents = "";
        for(String record:historyChatRecords){
            contents += record;
            contents += "\n";
        }
        Log.d("history", contents);

        Context context = getBaseContext();

        try {
            Log.d("Open","try to open local history");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_APPEND));
            outputStreamWriter.append(contents);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    private ArrayList<ChatRecord> readFromFile(String filename) {

        Context context = getBaseContext();
        ArrayList<String> re = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    re.add(receiveString);
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        ArrayList<ChatRecord> records = new ArrayList<>();
        for(String item:re){
//            item = item.trim();
//            Log.d("READHistory",item);
            String[] items = item.split(" ");
            // this can be improved by using substring to index the uid and get the type in index+1
            //the left string should be msg in the end. The current version may occur errors
            // when there are spaces in the head of msg like "  hello".
//            if(items.length != 3)continue;
            int type = Integer.parseInt(items[1]);
            String tempAvatar = avatar_mine;
            if(type==0||type==2){
                tempAvatar = avatar;
            }
            ChatRecord record = new ChatRecord(items[0], tempAvatar, Integer.parseInt(items[1]), items[3]);
            records.add(record);
        }
        return records;
    }

    private void selectImg(){
        PackageManager pm = getPackageManager();
        int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
        if (hasPerm == PackageManager.PERMISSION_GRANTED){
            final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatWindowActivity.this);
            builder.setTitle("Select Option");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (options[item].equals("Take Photo")) {
                        dialog.dismiss();
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT);
//                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
////                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO);
////                        Uri uri;
//                        String imageFileName = "JPEG_" + counter; //make a better file name
//                        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                        try{
//                            File image = File.createTempFile(imageFileName,
//                                    ".jpg",
//                                    storageDir
//                            );
//                            uri = Uri.fromFile(image);
//                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
////                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(takePhotoIntent, TAKE_PHOTO);
//                        }
//                        catch (IOException e){
//                            Log.e("IO",e.toString());
//                        }


                    } else if (options[item].equals("Choose From Gallery")) {
                        dialog.dismiss();
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, FROM_GALLERY);
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }
        else{
            String[] permissions = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this,permissions,3);
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        }

    }

    // for image pick up
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    uploadImg(bitmap);
                    if(bitmap==null)Log.e("BITMAP","NULL");

                }
                else{
                    Log.e("NOT","NOTCOMPLETE");
                }
                break;
            case FROM_GALLERY:
                if(requestCode ==RESULT_OK){
                    uri = data.getData();
                    Log.d("ImageURI",uri.toString());

                    String chatPath = "chatImg/";
                    StorageReference chatImgRef = FirebaseStorage.getInstance().getReference().child(chatPath);
                    Log.e("Storage",chatImgRef.toString());
                    UploadTask uploadTask = chatImgRef.putFile(uri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("UPLOAD","FAILED");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            String URL = taskSnapshot.getStorage().getDownloadUrl().toString();
                            String URL = taskSnapshot.getTask().getResult().toString();
                            Log.e("UPLOAD","SUCCESS");
                            Log.d("URlIS",URL);
                            Log.d("JJJJHH","okkkk");
                            ChatRecord record = new ChatRecord(uid, avatar_mine, 3, URL);
                            SendMessage(uid, uidChatWith, URL, "1");
                            // add to local history
                            String history = uid + " 3 " + "1 " + URL;
                            historyChatRecords.add(history);
//                            chatRecordsAdapter.updateRecords(record);
//                            recyclerView.setAdapter(chatRecordsAdapter);
                        }
                    });

                }
             break;
            default:
                break;
        }
    }

    public void setMyAvatar(String uid){
        Log.d("USERINFO",uid);
        String re = database.toString();
        Log.d("REEE",re);
        DatabaseReference userRef = myRef.child("new_Users").child(uid);

        final User[] user = new User[1];
        Log.d("REFFFF",userRef.toString());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    user[0] = task.getResult().getValue(User.class);
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    avatar_mine = user[0].getUrl();
                    ArrayList<ChatRecord> localHistory = readFromFile(filename);
                    chatRecordsAdapter = new ChatRecordsAdapter(getApplicationContext(), localHistory);
                    recyclerView.setAdapter(chatRecordsAdapter);
                }
            }
        });
    }

    private void uploadImg(Bitmap img){
//        InputStream stream = new FileInputStream(img);
        Uri uri_ = getImageUri(ChatWindowActivity.this,img);
        Log.e("URI",uri_.toString());
        String chatPath = "chatImg/";
        StorageReference chatImgRef = FirebaseStorage.getInstance().getReference().child(chatPath);
        Log.e("Storage",chatImgRef.toString());
        UploadTask uploadTask = chatImgRef.putFile(uri_);


        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return chatImgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String URL = task.getResult().toString();
                    Log.e("UPLOAD","SUCCESS");
                    ChatRecord record = new ChatRecord(uid, avatar_mine, 3,URL);
                    SendMessage(uid, uidChatWith, URL, "1");
                    // add to local history
                    String history = uid + " 3 " + "1 " + URL;
                    historyChatRecords.add(history);
//                    chatRecordsAdapter.updateRecords(record);
//                    recyclerView.setAdapter(chatRecordsAdapter);
                }
            }
        });

//        uploadTask.continueWithTask(new Continuation<UploadTask, Task<? extends Object>>() {
//            @Override
//            public Task<? extends Object> then(@NonNull Task<TaskSnapshot> task) throws Exception {
//                return null;
//            }
//        }).addOnCompleteListener(new OnCompleteListener<ContinuationResultT>() {
//            @Override
//            public void onComplete(@NonNull Task<ContinuationResultT> task) {
//                String URL = taskSnapshot.getTask().getResult().toString();
//                Log.e("UPLOAD","SUCCESS");
//                ChatRecord record = new ChatRecord(uid, avatar_mine, 3,URL);
//                SendMessage(uid, uidChatWith, URL, "1");
//                // add to local history
//                String history = uid + " 3 " + "1 " + URL;
//                historyChatRecords.add(history);
//                chatRecordsAdapter.updateRecords(record);
//                recyclerView.setAdapter(chatRecordsAdapter);
//            }
//        })

//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e("UPLOAD","FAILED");
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                String URL = taskSnapshot.getStorage().getDownloadUrl().toString();
//                String URL = taskSnapshot.getTask().getResult().toString();
//                Log.e("UPLOAD","SUCCESS");
//                ChatRecord record = new ChatRecord(uid, avatar_mine, 3,URL);
//                SendMessage(uid, uidChatWith, URL, "1");
//                // add to local history
//                String history = uid + " 3 " + "1 " + URL;
//                historyChatRecords.add(history);
//                chatRecordsAdapter.updateRecords(record);
//                recyclerView.setAdapter(chatRecordsAdapter);
//            }
//        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}