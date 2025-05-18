package com.example.denticaree;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registerDoctor extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, secretCodeInput, specialiteInput;
    private Button registerButton;
    private TextView tvLogin;
    private final String CODE_SECRET = "DOC2024"; // à sécuriser

    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doctor);

        // Initialisation des vues
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        secretCodeInput = findViewById(R.id.secretCodeInput);
        specialiteInput = findViewById(R.id.specialiteInput);
        registerButton = findViewById(R.id.registerButton);
        tvLogin = findViewById(R.id.textView);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String code = secretCodeInput.getText().toString().trim();
            String specialite = specialiteInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || code.isEmpty() || specialite.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!code.equals(CODE_SECRET)) {
                Toast.makeText(this, "Code secret invalide", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();
                        Map<String, Object> doctor = new HashMap<>();
                        doctor.put("nom", name);
                        doctor.put("email", email);
                        doctor.put("specialite", specialite);
                        doctor.put("role", "doctor");

                        firestore.collection("users").document(uid)
                                .set(doctor)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, LoginDoctorActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Erreur Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erreur Auth: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        tvLogin.setOnClickListener(v -> {
            Intent i2 = new Intent(registerDoctor.this, LoginDoctorActivity.class);
            startActivity(i2);
        });
    }
}
