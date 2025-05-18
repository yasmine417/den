package com.example.denticaree;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    private EditText etName, etMail, etPassword, etPassword1;
    private Button bRegister;
    private TextView tvLogin;
    private FirebaseAuth myAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_register);

        // Initialisation
        etName = findViewById(R.id.signupFullName);
        etMail = findViewById(R.id.signupEmailInput);
        etPassword = findViewById(R.id.signupPasswordInput);
        etPassword1 = findViewById(R.id.confirmpassword);
        bRegister = findViewById(R.id.signUpConfirmButton);
        tvLogin = findViewById(R.id.textView);

        myAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Action du bouton d'inscription
        bRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mail = etMail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etPassword1.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mail) ||
                    TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            signUp(name, mail, password);
        });

        // Lien pour retourner au login
        tvLogin.setOnClickListener(v -> {
            Intent i2 = new Intent(register.this, LoginPatientActivity.class);
            startActivity(i2);
        });
    }

    // Enregistrement de l'utilisateur
    private void signUp(String fullName, String mail, String password) {
        myAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Enregistrement réussi, sauvegarder les infos supplémentaires dans Firestore
                        String uid = myAuth.getCurrentUser().getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("fullName", fullName);
                        userMap.put("email", mail);
                        userMap.put("role", "patient");

                        firestore.collection("users").document(uid)
                                .set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(register.this, LoginPatientActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getApplicationContext(), "Error saving user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    } else {
                        Toast.makeText(getApplicationContext(), "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
