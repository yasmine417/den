package com.example.denticaree;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class AddAvailabilityActivity extends AppCompatActivity {

    private TextView dateTextView, timeTextView;
    private Button addButton;

    private FirebaseFirestore db;
    private String doctorId;

    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_availability); // crée ce layout !

        db = FirebaseFirestore.getInstance();
        doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        addButton = findViewById(R.id.addButton);

        dateTextView.setOnClickListener(v -> showDatePicker());
        timeTextView.setOnClickListener(v -> showTimePicker());

        addButton.setOnClickListener(v -> addAvailability());

    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            year = y;
            month = m;
            day = d;
            dateTextView.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, h, m) -> {
            hour = h;
            minute = m;
            timeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void addAvailability() {
        if (dateTextView.getText().toString().isEmpty() || timeTextView.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez choisir une date et une heure", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        Date timestamp = calendar.getTime();

        Map<String, Object> availability = new HashMap<>();
        availability.put("doctorId", doctorId);
        availability.put("timestamp", timestamp);
        availability.put("date", dateTextView.getText().toString());
        availability.put("time", timeTextView.getText().toString());

        db.collection("availabilities")
                .add(availability)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Disponibilité ajoutée", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
                });
    }
}
