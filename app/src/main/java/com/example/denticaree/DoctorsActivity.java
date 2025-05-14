package com.example.denticaree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DoctorsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView doctorsList;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        db = FirebaseFirestore.getInstance();
        doctorsList = findViewById(R.id.doctorsList);
        backButton = findViewById(R.id.backButton);

        db.collection("doctors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder doctors = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            doctors.append(document.getId()).append(": ").append(document.getData()).append("\n");
                        }
                        doctorsList.setText(doctors.toString());
                    } else {
                        Log.w("Firestore", "Error getting doctors: ", task.getException());
                        doctorsList.setText("Error loading doctors");
                    }
                });

        // زر الرجوع
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}