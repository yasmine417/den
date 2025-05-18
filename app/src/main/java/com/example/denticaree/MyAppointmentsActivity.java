package com.example.denticaree;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyAppointmentsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView upcomingAppointmentsTextView;
    private TextView pastAppointmentsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        upcomingAppointmentsTextView = findViewById(R.id.upcomingAppointments);
        pastAppointmentsTextView = findViewById(R.id.pastAppointments);

        loadAppointments();
    }

    private void loadAppointments() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("appointments")
                .whereEqualTo("patientId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    StringBuilder upcoming = new StringBuilder();
                    StringBuilder past = new StringBuilder();
                    Date now = new Date();

                    SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    if (queryDocumentSnapshots.isEmpty()) {
                        upcomingAppointmentsTextView.setText("Aucun rendez-vous à venir.");
                        pastAppointmentsTextView.setText("Aucun rendez-vous passé.");
                        return;
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String dateStr = doc.getString("date");
                        String timeStr = doc.getString("time");
                        String doctorId = doc.getString("doctorId");
                        String status = doc.getString("status");

                        String dateTimeStr = dateStr + " " + (timeStr != null ? timeStr : "00:00");
                        Date appointmentDate = null;
                        try {
                            SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            appointmentDate = sdfDateTime.parse(dateTimeStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        final String displayDate = appointmentDate != null ? sdfDisplay.format(appointmentDate) : dateTimeStr;

                        // Récupérer le nom du docteur
                        Date finalAppointmentDate = appointmentDate;
                        db.collection("users").document(doctorId).get()
                                .addOnSuccessListener(userDoc -> {
                                    String doctorName = "Nom inconnu";
                                    if (userDoc.exists()) {
                                        String nom = userDoc.getString("nom");
                                        if (nom != null && !nom.isEmpty()) {
                                            doctorName = nom;
                                        }
                                    }

                                    String statusEmoji = "✅";
                                    if ("canceled".equalsIgnoreCase(status) || "annulé".equalsIgnoreCase(status)) {
                                        statusEmoji = "📌";
                                    }

                                    String appointmentText = (finalAppointmentDate != null && finalAppointmentDate.after(now) ? "🗓 Date : " : "📅 Date : ")
                                            + displayDate + "\n"
                                            + "👨‍⚕️ Docteur : " + doctorName + "\n"
                                            + statusEmoji + " Statut : " + (status != null ? status : "inconnu") + "\n\n";

                                    if (finalAppointmentDate != null && finalAppointmentDate.after(now)) {
                                        synchronized (upcoming) {
                                            upcoming.append(appointmentText);
                                            runOnUiThread(() -> upcomingAppointmentsTextView.setText(upcoming.toString()));
                                        }
                                    } else {
                                        synchronized (past) {
                                            past.append(appointmentText);
                                            runOnUiThread(() -> pastAppointmentsTextView.setText(past.toString()));
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firebase", "Erreur récupération nom docteur", e));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erreur de chargement des rendez-vous", e);
                    upcomingAppointmentsTextView.setText("Erreur de chargement.");
                    pastAppointmentsTextView.setText("Erreur de chargement.");
                });
    }
}
