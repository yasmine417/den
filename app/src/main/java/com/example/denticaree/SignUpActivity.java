package com.example.denticaree;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText signupEmailInput, signupPasswordInput;
    private Button signUpConfirmButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // تهيئة Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // ربط العناصر من الـ XML
        signupEmailInput = findViewById(R.id.signupEmailInput);
        signupPasswordInput = findViewById(R.id.signupPasswordInput);
        signUpConfirmButton = findViewById(R.id.signUpConfirmButton);
        backButton = findViewById(R.id.backButton);

        // زر التسجيل
        signUpConfirmButton.setOnClickListener(v -> {
            String email = signupEmailInput.getText().toString().trim();
            String password = signupPasswordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                signUpUser(email, password);
            }
        });

        // زر الرجوع
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signUpUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Sign Up successful: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign Up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}