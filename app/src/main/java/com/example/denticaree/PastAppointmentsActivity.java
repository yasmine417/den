package com.example.denticaree;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PastAppointmentsActivity extends AppCompatActivity {

    private ListView lvPastAppointments;
    private PastAppointmentAdapter adapter;
    private List<AppointmentDetails> appointments;
    private FirebaseFirestore db;
    private String doctorUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_appointments);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("PastAppointmentsActivity", "Utilisateur non connecté");
            Toast.makeText(this, "Session expirée, veuillez vous reconnecter", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        doctorUid = currentUser.getUid();
        Log.d("PastAppointmentsActivity", "Doctor UID: " + doctorUid);

        lvPastAppointments = findViewById(R.id.lvPastAppointments);
        appointments = new ArrayList<>();
        adapter = new PastAppointmentAdapter(this, appointments);
        lvPastAppointments.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadPastAppointments();
    }

    private void loadPastAppointments() {
        appointments.clear();
        adapter.notifyDataSetChanged();
        Log.d("PastAppointmentsActivity", "Début du chargement des rendez-vous passés");

        // Requête pour status = completed
        db.collection("appointments")
                .whereEqualTo("doctorId", doctorUid)
                .whereEqualTo("status", "completed")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("PastAppointmentsActivity", "Rendez-vous completed trouvés : " + querySnapshot.size());
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        addAppointmentFromDoc(doc);
                    }
                    // Requête pour status = rescheduled
                    db.collection("appointments")
                            .whereEqualTo("doctorId", doctorUid)
                            .whereEqualTo("status", "follow-up")
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(querySnapshot2 -> {
                                Log.d("PastAppointmentsActivity", "Rendez-vous rescheduled trouvés : " + querySnapshot2.size());
                                for (DocumentSnapshot doc : querySnapshot2.getDocuments()) {
                                    addAppointmentFromDoc(doc);
                                }
                                adapter.notifyDataSetChanged();
                                if (appointments.isEmpty()) {
                                    Log.w("PastAppointmentsActivity", "Aucun rendez-vous passé trouvé");
                                    Toast.makeText(this, "Aucun rendez-vous passé trouvé", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erreur chargement rendez-vous follow-up: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace(); // Cela affichera dans Logcat le lien Firestore vers l'index à créer
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur chargement rendez-vous completed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace(); // Cela affichera dans Logcat le lien Firestore vers l'index à créer
                });

    }

    private void addAppointmentFromDoc(DocumentSnapshot doc) {
        String date = doc.getString("date");
        String time = doc.getString("time");
        String motif = doc.getString("motif");
        String traitement = doc.getString("traitement");
        String status = doc.getString("status");
        String patientId = doc.getString("patientId");

        if (patientId == null) {
            Log.w("PastAppointmentsActivity", "patientId manquant");
            return;
        }

        // Récupérer nom + email depuis la collection users
        db.collection("users").document(patientId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    String patientName = userDoc.getString("fullName");
                    String patientEmail = userDoc.getString("email");

                    appointments.add(new AppointmentDetails(date, time, motif, traitement,
                            patientName, patientEmail, status));

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("PastAppointmentsActivity", "Erreur lors de la récupération du patient : " + e.getMessage());
                    Toast.makeText(this, "Impossible de récupérer les infos du patient", Toast.LENGTH_SHORT).show();
                });
    }


}