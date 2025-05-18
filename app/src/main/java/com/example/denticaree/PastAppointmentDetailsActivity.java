package com.example.denticaree;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PastAppointmentDetailsActivity extends AppCompatActivity {

    private TextView tvDate, tvTime, tvMotif, tvTraitement;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_appointment_details);

        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvMotif = findViewById(R.id.tvMotif);
        tvTraitement = findViewById(R.id.tvTraitement);

        db = FirebaseFirestore.getInstance();

        String appointmentId = getIntent().getStringExtra("appointmentId");
        if (appointmentId == null) {
            Toast.makeText(this, "Aucun rendez-vous sélectionné", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAppointmentDetails(appointmentId);
    }

    private void loadAppointmentDetails(String appointmentId) {
        DocumentReference docRef = db.collection("appointments").document(appointmentId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String date = documentSnapshot.getString("date");
                String time = documentSnapshot.getString("time");
                String motif = documentSnapshot.getString("motif");
                String traitement = documentSnapshot.getString("traitement");

                tvDate.setText("Date : " + (date != null ? date : "Non spécifié"));
                tvTime.setText("Heure : " + (time != null ? time : "Non spécifié"));
                tvMotif.setText("Motif : " + (motif != null ? motif : "Non renseigné"));
                tvTraitement.setText("Traitement : " + (traitement != null ? traitement : "Non renseigné"));
            } else {
                Toast.makeText(this, "Rendez-vous non trouvé", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
