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

        // Charger les docteurs (depuis "users" où role = doctor)
        db.collection("users")
                .whereEqualTo("role", "doctor")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder doctors = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nom = document.getString("nom");
                            String specialite = document.getString("specialite");
                            String email = document.getString("email");

                            doctors.append("👨‍⚕️ Nom : ").append(nom).append("\n")
                                    .append("🦷 Spécialité : ").append(specialite).append("\n")
                                    .append("📧 Email : ").append(email).append("\n\n");
                        }
                        doctorsList.setText(doctors.toString());
                    } else {
                        Log.w("Firestore", "Erreur de chargement des docteurs : ", task.getException());
                        doctorsList.setText("Erreur lors du chargement des docteurs");
                    }
                });

        // Bouton retour au profil
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
