package com.example.denticaree;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText patientIdEditText, doctorIdEditText, dateEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = FirebaseFirestore.getInstance();

        patientIdEditText = findViewById(R.id.patientId);
        doctorIdEditText = findViewById(R.id.doctorId);
        dateEditText = findViewById(R.id.date);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String patientId = patientIdEditText.getText().toString();
            String doctorId = doctorIdEditText.getText().toString();
            String date = dateEditText.getText().toString();

            Map<String, Object> appointment = new HashMap<>();
            appointment.put("patientId", patientId);
            appointment.put("doctorId", doctorId);
            appointment.put("date", date);
            appointment.put("status", "pending");

            db.collection("appointments")
                    .add(appointment)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Appointment added with ID: " + documentReference.getId());
                        finish(); // إغلاق النشاط بعد النجاح
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error adding appointment", e);
                    });
        });
    }
}