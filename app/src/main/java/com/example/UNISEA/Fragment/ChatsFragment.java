package com.example.UNISEA.Fragment;

import android.content.Context;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.UNISEA.Adapter.ChatAdapter;
import com.example.UNISEA.Model.Chat;
import com.example.UNISEA.Model.ChatRecord;
import com.example.UNISEA.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.UNISEA.Model.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    public List<Chat> Chats;
    private DatabaseReference myRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String uid;

    //TODO: for test, delete this in final version
    private String uid_chatWith;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use local db to test
        // TODO: replace to online db in final version
//        database.useEmulator("10.0.2.2", 9000);
        myRef = database.getReference();

        uid = FirebaseAuth.getInstance().getUid();
//        uid = "1vbt0jmg38TJEeJKTJl7NEI06AJ2";  //for test
//        uid_chatWith = "3RoYQ0k88KSYlXH1GWxIKUi53xi1";


//        uid = "3vheCH6jreaIhfgRuSlHTQvXc032";
//        uid_chatWith = "DC3kduN9RWOkeYnqAt5a3CKbiRB3";

        Chats = new ArrayList<>();

        // TODO: complete the readChats function to read the chats of the user from the firebase
        readChats();


    }

    @Override
    public void onResume(){
        super.onResume();
//        loadLocal();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chats, container, false);

        // Bind the recycler view
        recyclerView = view.findViewById(R.id.Chat_List);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatAdapter = new ChatAdapter(getContext(), Chats);
        recyclerView.setAdapter(chatAdapter);

        return view;
    }


    // Load unread chat records from db and put them in Chat fragment of the Main page.
    private void readChats() {

        // delete this in final version, this is for test
//        addNewChat(uid_chatWith,"test",1);

        // db refer to the unread msgs for current user's UID
        DatabaseReference msgQueRef = myRef.child("Msg").child(uid).child("UnreadMsg");
        Log.d("REF",myRef.toString());
        Log.d("READ","EAD");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    HashMap<String, Object> msgQueue = (HashMap<String, Object>)snapshot.getValue();
                    if(msgQueue==null)return; // no unread msgs
                    for(Object uid:msgQueue.keySet()){
                        HashMap<String, Object> queue = (HashMap<String, Object>) msgQueue.get(uid);
                        ArrayList<String> msgs = (ArrayList<String>)queue.values().iterator().next();
                        int size = msgs.size();

                        if(size==0){
                            Log.d("Sizecheck","0");
                            Chat chat = findRecordByUid(Chats, uid.toString());
                            if(chat!=null)chat.setUnread_num(0);
                        }
                        String lastRecord = msgs.get(size-1).split(" ")[1];
                        Log.d("UIDcheck",uid.toString());
                        Chat chat = findRecordByUid(Chats, uid.toString());
                        // find the basic information of the uid.
//                        User userInfo = getUserInfo(uid.toString());
                        if(chat == null){
                            addNewChat(uid.toString(),lastRecord,size);
                            Log.d("FINDChat","False");
//                            Chat chat_ = new Chat(uid.toString(), userInfo.getUrl(), userInfo.getUsername(), lastRecord, "18/10/2022", size);
//                            Chats.add(chat_);
                        }
                        else{
                            chat.setLatest_chat(lastRecord);
                            chat.setUnread_num(size);
                        }

                        // set the num of unread msg to 0 if there is no unread msg for some friends
                        String[] uids = msgQueue.keySet().toArray(new String[0]);  // the uids which have new msgs
                        ArrayList<String> uids_arr = new ArrayList<>(Arrays.asList(uids));
//                        Log.d("UIDsContains",Arrays.toString(uids));

                        for(Chat c:Chats){
                            if(uids_arr.contains(c.getUID()))continue;
                            c.setUnread_num(0);
                        }
                    }


                }
                else{
                    for(Chat c:Chats){
                        c.setUnread_num(0);
                    }
                }
                loadLocal();
                chatAdapter = new ChatAdapter(getContext(), Chats);
                recyclerView.setAdapter(chatAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MSG QUEUE","removed");
            }
        };

        msgQueRef.addValueEventListener(listener);


    }


    private Chat findRecordByUid(List<Chat> chats, String uid){
        for(Chat record: chats){
            Log.d("recordUID",record.getUID());
            if(Objects.equals(uid, record.getUID()))return record;
        }
        return null;
    }

    // get the basic user info from database, like the avatar and username
    public User getUserInfo(String uid){
        Log.d("USERINFO",uid);
        String re = database.toString();
        Log.d("REEE",re);
        HashMap<String, String> info = new HashMap<>();
        DatabaseReference userRef = myRef.child("new_Users").child(uid);

        final User[] user = new User[1];
        user[0] = new User("uu","uuuu","uuuu",2);
        Log.d("REFFFF",userRef.toString());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    user[0] = null;
                }
                else {
                    user[0] = task.getResult().getValue(User.class);
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
        return user[0];
    }

    public void addNewChat(String uid, String lastRecord, int size){
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
                    user[0] = null;
                }
                else {
                    user[0] = task.getResult().getValue(User.class);
                    Chat chat_ = new Chat(uid.toString(), user[0].getUrl(), user[0].getUsername(), lastRecord, "18/10/2022", size);
                    Chats.add(chat_);
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    chatAdapter = new ChatAdapter(getContext(), Chats);
                    recyclerView.setAdapter(chatAdapter);
                }
            }
        });

    }

    private void loadLocal(){
        DatabaseReference userRef = myRef.child("Friends").child(uid);

//        final User[] user = new User[1];
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
//                    user[0] = null;
                }
                else {
                    HashMap<String, Object> users = (HashMap<String, Object>)task.getResult().getValue();
                    if(users==null)return;
                    for(Object uid:users.keySet()){
                        if(findRecordByUid(Chats,uid.toString())!=null)continue;
                        String filename ="ChatHitory" + "_" + uid.toString() + "_.txt";
                        readFromFile(filename);
                    }
//                    Chat chat_ = new Chat(uid.toString(), user[0].getUrl(), user[0].getUsername(), lastRecord, "18/10/2022", size);
//                    Chats.add(chat_);
//                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                    chatAdapter = new ChatAdapter(getContext(), Chats);
//                    recyclerView.setAdapter(chatAdapter);
                }
            }
        });
    }
    private void readFromFile(String filename) {

        Context context = this.getContext();
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
        int size = re.size();
        if(findRecordByUid(Chats,filename.split("_")[1])!=null)return;
        if(size==0)return;
        String last = re.get(re.size()-1).split(" ")[3];
        addNewChat(filename.split("_")[1],last,0);
//        Chat chat_ = new Chat(uid.toString(), user[0].getUrl(), user[0].getUsername(), lastRecord, "18/10/2022", size);
//        Chats.add(chat_);
//        chatAdapter = new ChatAdapter(getContext(), Chats);
//        recyclerView.setAdapter(chatAdapter);
//        for(String item:re){
////            item = item.trim();
//            Log.d("READ History",item);
//            String[] items = item.split(" ");
//            // this can be improved by using substring to index the uid and get the type in index+1
//            //the left string should be msg in the end. The current version may occur errors
//            // when there are spaces in the head of msg like "  hello".
//            if(items.length != 3)continue;
//            int type = Integer.parseInt(items[1]);
//            String tempAvatar = type==0?avatar:avatar_mine;
//            ChatRecord record = new ChatRecord(items[0], tempAvatar, Integer.parseInt(items[1]), items[2]);
//            records.add(record);
//        }
//        return records;
    }

}