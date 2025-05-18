// ConsultationActivity.java
package com.example.denticaree;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConsultationActivity extends AppCompatActivity {

    EditText etMotif, etTraitement;
    TextView tvInfos;
    Button btnTerminer, btnFollowUp;
    FirebaseFirestore db;

    String appointmentId, patientId, doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);

        etMotif = findViewById(R.id.etMotif);
        etTraitement = findViewById(R.id.etTraitement);
        tvInfos = findViewById(R.id.tvInfos);
        btnTerminer = findViewById(R.id.btnTerminer);
        btnFollowUp = findViewById(R.id.btnFollowUp);

        db = FirebaseFirestore.getInstance();

        appointmentId = getIntent().getStringExtra("appointmentId");
        patientId = getIntent().getStringExtra("patientId");
        doctorId = getIntent().getStringExtra("doctorId");

        tvInfos.setText("Patient ID : " + patientId);

        btnTerminer.setOnClickListener(v -> mettreAJourRdv("completed"));
        btnFollowUp.setOnClickListener(v -> mettreAJourRdv("follow-up"));
    }

    private void mettreAJourRdv(String statut) {
        String motif = etMotif.getText().toString().trim();
        String traitement = etTraitement.getText().toString().trim();

        if (motif.isEmpty() || traitement.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference rdvRef = db.collection("appointments").document(appointmentId);

        Map<String, Object> update = new HashMap<>();
        update.put("motif", motif);
        update.put("traitement", traitement);
        update.put("status", statut);

        rdvRef.update(update)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Rendez-vous mis à jour", Toast.LENGTH_SHORT).show();

                    if (statut.equals("follow-up")) {
                        creerRdvSuivi();
                    }

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void creerRdvSuivi() {
        Map<String, Object> newRdv = new HashMap<>();
        newRdv.put("patientId", patientId);
        newRdv.put("doctorId", doctorId);
        newRdv.put("status", "pending");
        newRdv.put("date", "à définir");
        newRdv.put("time", "à définir");
        newRdv.put("timestamp", FieldValue.serverTimestamp());

        db.collection("appointments")
                .add(newRdv)
                .addOnSuccessListener(doc ->
                        Toast.makeText(this, "Rendez-vous de suivi créé", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur suivi : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
