package com.example.UNISEA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.UNISEA.Adapter.ContactAdapter;
import com.example.UNISEA.Adapter.StepAdapter;
import com.example.UNISEA.Model.Contact;
import com.example.UNISEA.Model.Step;

import java.util.ArrayList;
import java.util.List;

public class StepActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView my_step;
    private RecyclerView step_list;
    private StepAdapter stepAdapter;
    private List<Step> Steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        // back to the sign in page
        backButton = findViewById(R.id.BackBtn);
        backButton.setOnClickListener(v -> onBackPressed());

        my_step = findViewById(R.id.my_step);
        // TODO: read my step
        my_step.setText("2333");

        step_list = findViewById(R.id.step_list);
        step_list.setHasFixedSize(true);
        step_list.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

        Steps = new ArrayList<>();

        // TODO: complete the readContacts function to read the contacts of the user from the firebase
        readSteps();

        stepAdapter = new StepAdapter(this.getApplicationContext(), Steps);
        step_list.setAdapter(stepAdapter);

    }

    private void readSteps() {
        Step contact1 = new Step("asdafwqefwedvads", "./res/drawable/chat1.jpg", "User1", 1, 555);
        Steps.add(contact1);

        Step contact2 = new Step("vavdafqwefa", "./res/drawable/chat1.jpg", "User2", 2, 4444);
        Steps.add(contact2);

        Step contact3 = new Step("vadsvweghrfgn", "./res/drawable/chat1.jpg", "User3", 3, 22);
        Steps.add(contact3);

        Step contact4 = new Step("ljknelfnvsdklv", "./res/drawable/chat1.jpg", "User4", 4, 12345);
        Steps.add(contact4);

        Step contact5= new Step("avlkdnojnewion", "./res/drawable/chat1.jpg", "AKA", 5, 999);
        Steps.add(contact5);

        Step contact6 = new Step("vajbduieo", "./res/drawable/chat1.jpg", "User6", 6, 7777);
        Steps.add(contact6);

        Step contact7 = new Step("vaonweiojf", "./res/drawable/chat1.jpg", "User7", 7, 64312);
        Steps.add(contact7);

        Step contact8 = new Step("aoevheiog", "./res/drawable/chat1.jpg", "User8", 8, 6);
        Steps.add(contact8);

        Step contact9 = new Step("asdafwqefwedvads", "./res/drawable/chat1.jpg", "User9", 9, 8888);
        Steps.add(contact9);

        Step contact10 = new Step("asdafwqefwedvads", "./res/drawable/chat1.jpg", "Okk", 10, 42);
        Steps.add(contact10);
    }

    // Return to the ChatsFragment
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StepActivity.this, MainActivity.class);
        intent.putExtra("id",5);
        startActivity(intent);
        super.onBackPressed();
    }
}