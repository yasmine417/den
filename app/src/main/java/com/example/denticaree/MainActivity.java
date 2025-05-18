package com.example.denticaree;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    RadioGroup userTypeGroup;
    RadioButton radioStudent, radioTeacher;
    Button continueButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        userTypeGroup = findViewById(R.id.userTypeGroup);
        radioStudent = findViewById(R.id.radioStudent);
        radioTeacher = findViewById(R.id.radioTeacher);
        continueButton = findViewById(R.id.continueButton);

        continueButton.setOnClickListener(v -> {
            int selectedId = userTypeGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(MainActivity.this, "Veuillez choisir un rôle", Toast.LENGTH_SHORT).show();
            } else if (selectedId == R.id.radioStudent) {

                Intent intent = new Intent(MainActivity.this, LoginPatientActivity.class);
                startActivity(intent);
            } else if (selectedId == R.id.radioTeacher) {

                Intent intent = new Intent(MainActivity.this, LoginDoctorActivity.class);
                startActivity(intent);  }
        });
    }
}
