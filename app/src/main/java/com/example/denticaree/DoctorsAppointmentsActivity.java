package com.example.denticaree;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DoctorsAppointmentsActivity extends AppCompatActivity {

    private ListView appointmentsListView;
    private FirebaseFirestore db;
    private String doctorUid;
    private Button addButton1;
    private Button addButton2;
    private Button addButton;

    private AppointmentAdapter adapter;
    private List<AppointmentItem> appointmentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_appointments);
        addButton1= findViewById(R.id.addButton);
        addButton2= findViewById(R.id.addButton1);
        addButton= findViewById(R.id.addButton2);
        appointmentsListView = findViewById(R.id.appointmentsListView);
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Session expirée", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        doctorUid = currentUser.getUid();
        appointmentItems = new ArrayList<>();
        adapter = new AppointmentAdapter(this, appointmentItems, this::updateAppointmentStatus);
        appointmentsListView.setAdapter(adapter);
        addButton1.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorsAppointmentsActivity.this, AddAvailabilityActivity.class);
            startActivity(intent);
        });
        addButton2.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorsAppointmentsActivity.this, AcceptedAppointmentsActivity.class);
            startActivity(intent);
        });
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorsAppointmentsActivity.this, PastAppointmentsActivity.class);
            startActivity(intent);
        });
        loadAppointments();
    }

    private void loadAppointments() {
        db.collection("appointments")
                .whereEqualTo("doctorId", doctorUid)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    appointmentItems.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String appointmentId = doc.getId();
                        String patientId = doc.getString("patientId");
                        String date = doc.getString("date");
                        String time = doc.getString("time");

                        db.collection("users").document(patientId)
                                .get()
                                .addOnSuccessListener(patientDoc -> {
                                    String fullName = patientDoc.getString("fullName");
                                    if (fullName == null) fullName = "Nom inconnu";

                                    appointmentItems.add(new AppointmentItem(appointmentId, patientId, fullName, date, time));
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Erreur récupération patient", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur chargement rendez-vous", Toast.LENGTH_SHORT).show());
    }


    private void updateAppointmentStatus(String appointmentId, String patientId, String newStatus) {
        DocumentReference docRef = db.collection("appointments").document(appointmentId);

        docRef.update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    // Mise à jour aussi dans le document du patient
                    db.collection("patients")
                            .document(patientId)
                            .collection("appointments")
                            .document(appointmentId)
                            .update("status", newStatus);

                    Toast.makeText(this, "Rendez-vous " + newStatus, Toast.LENGTH_SHORT).show();
                    loadAppointments(); // recharge la liste
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur mise à jour", Toast.LENGTH_SHORT).show();
                });
    }
}
