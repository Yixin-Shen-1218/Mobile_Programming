package com.example.UNISEA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.UNISEA.Model.Preference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditMatchProfileActivity extends AppCompatActivity {
    private TextInputEditText Age_1_Input;
    private TextInputEditText Age_2_Input;
    private TextInputEditText Height_1_Input;
    private TextInputEditText Height_2_Input;
    private MaterialCheckBox venus_checkbox;
    private MaterialCheckBox mars_checkbox;
    private MaterialCheckBox non_bi_checkbox;
    private TextInputEditText Distance_Input;
    private Button saveButton;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_match_profile);

        uid = FirebaseAuth.getInstance().getUid();

        ImageView backBtn = findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> onBackPressed());

        Age_1_Input = findViewById(R.id.Age_1_Input);
        Age_2_Input = findViewById(R.id.Age_2_Input);

        Height_1_Input = findViewById(R.id.Height_1_Input);
        Height_2_Input = findViewById(R.id.Height_2_Input);

        venus_checkbox = findViewById(R.id.venus_checkbox);
        mars_checkbox = findViewById(R.id.mars_checkbox);
        non_bi_checkbox = findViewById(R.id.non_bi_checkbox);

        Distance_Input = findViewById(R.id.Distance_Input);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> SavePreference());

        loadPreference();
    }

    private void loadPreference() {
        FirebaseDatabase.getInstance().getReference("Preference").child(uid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Preference preference = snapshot.getValue(Preference.class);
                if (preference != null) {
                    int age1 = preference.getAge1();
                    int age2 = preference.getAge2();
                    int height1 = preference.getHeight1();
                    int height2 = preference.getHeight2();
                    boolean female = preference.isFemale();
                    boolean male = preference.isMale();
                    boolean nonBinary = preference.isNonBinary();
                    int distance = preference.getDistance();

                    Age_1_Input.setText(String.valueOf(age1));
                    Age_2_Input.setText(String.valueOf(age2));
                    Height_1_Input.setText(String.valueOf(height1));
                    Height_2_Input.setText(String.valueOf(height2));
                    venus_checkbox.setChecked(female);
                    mars_checkbox.setChecked(male);
                    non_bi_checkbox.setChecked(nonBinary);
                    Distance_Input.setText(String.valueOf(distance));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void SavePreference() {

        int age1;
        int age2;
        int height1;
        int height2;
        int distance;

        // check age
        try {
            age1 = Integer.parseInt(Age_1_Input.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {
            Age_1_Input.setError("Please input a number!");
            Age_1_Input.requestFocus();
            return;
        }
        try {
            age2 = Integer.parseInt(Age_2_Input.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {
            Age_2_Input.setError("Please input a number!");
            Age_2_Input.requestFocus();
            return;
        }
        if (age2 < age1) {
            Toast.makeText(getApplicationContext(),"Age selection invalid!",Toast.LENGTH_LONG).show();
            return;
        }

        // check height
        try {
            height1 = Integer.parseInt(Height_1_Input.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {
            Height_1_Input.setError("Please input a number!");
            Height_1_Input.requestFocus();
            return;
        }
        try {
            height2 = Integer.parseInt(Height_2_Input.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {
            Height_2_Input.setError("Please input a number!");
            Height_2_Input.requestFocus();
            return;
        }
        if (height2 < height1) {
            Toast.makeText(getApplicationContext(),"Height selection invalid!",Toast.LENGTH_LONG).show();
            return;
        }

        // check gender
        boolean venusCheck = venus_checkbox.isChecked();
        boolean marsCheck = mars_checkbox.isChecked();
        boolean nonCheck = non_bi_checkbox.isChecked();
        if (!venusCheck && !marsCheck && !nonCheck) {
            Toast.makeText(getApplicationContext(),"Please select a gender!",Toast.LENGTH_LONG).show();
            return;
        }

        // check distance
        try {
            distance = Integer.parseInt(Distance_Input.getText().toString());
        } catch (NumberFormatException | NullPointerException e) {
            Distance_Input.setError("Please input a number!");
            Distance_Input.requestFocus();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(EditMatchProfileActivity.this);
        builder.setTitle("Save preference:");
        builder.setMessage("Do you really want to save your preference");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // save preference
                Preference preference = new Preference(age1, age2, height1, height2, marsCheck,
                        venusCheck, nonCheck, distance);
                FirebaseDatabase.getInstance().getReference("Preference").child(uid).setValue(preference)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(EditMatchProfileActivity.this,"Update success!",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
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


    @Override
    public void onBackPressed() {
        Log.d("EditMatchProfileActivity", "Return ");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("return", "Hello from the second activity.");
        setResult(RESULT_OK, returnIntent);

        super.onBackPressed();
    }
}