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

public class LoginDoctorActivity extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private Button loginButton;
    private TextView tvLogin;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_doctor);

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        tvLogin = findViewById(R.id.textView);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        tvLogin.setOnClickListener(v -> {
            Intent i2 = new Intent(LoginDoctorActivity.this, registerDoctor.class);
            startActivity(i2);
        });
        loginButton.setOnClickListener(v -> {
            String email = emailLogin.getText().toString().trim();
            String password = passwordLogin.getText().toString().trim();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();
                        firestore.collection("users").document(uid).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists() && "doctor".equals(documentSnapshot.getString("role"))) {
                                        Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                                        // Aller à l'accueil du docteur
                                        startActivity(new Intent(this, DoctorsAppointmentsActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Accès refusé : ce n'est pas un compte docteur", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}