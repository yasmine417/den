package com.example.denticaree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView profileName, profileEmail, profileInfo;
    private Button refreshButton, bookAppointmentButton, viewDoctorsButton, logoutButton;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // تهيئة Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // تهيئة العناصر
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileInfo = findViewById(R.id.profileInfo);
        refreshButton = findViewById(R.id.refreshButton);
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton);
        viewDoctorsButton = findViewById(R.id.viewDoctorsButton);
        logoutButton = findViewById(R.id.logoutButton);
        calendarView = findViewById(R.id.calendarView);

        // تحميل بيانات المستخدم عند بدء النشاط
        loadUserProfile();

        // التعامل مع زر التحديث
        refreshButton.setOnClickListener(v -> loadUserProfile());

        // التعامل مع زر جدولة موعد
        bookAppointmentButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, BookingActivity.class);
            startActivity(intent);
        });

        // التعامل مع زر عرض الأطباء
        viewDoctorsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, DoctorsActivity.class);
            startActivity(intent);
        });

        // التعامل مع زر تسجيل الخروج
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // التعامل مع اختيار تاريخ فـ التقويم
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            profileInfo.setText("Selected Date: " + selectedDate);
        });
    }

    private void loadUserProfile() {
        if (auth.getCurrentUser() == null) {
            profileInfo.setText("Please sign in to view profile");
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        Long age = documentSnapshot.getLong("age");

                        profileName.setText("Name: " + (name != null ? name : "N/A"));
                        profileEmail.setText("Email: " + (email != null ? email : "N/A"));
                        profileInfo.setText("Age: " + (age != null ? age : "N/A"));
                    } else {
                        profileInfo.setText("No profile data found");
                        profileName.setText("Name: Not Found");
                        profileEmail.setText("Email: Not Found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error loading profile: ", e);
                    profileInfo.setText("Error loading profile");
                    profileName.setText("Name: Error");
                    profileEmail.setText("Email: Error");
                });
    }
}