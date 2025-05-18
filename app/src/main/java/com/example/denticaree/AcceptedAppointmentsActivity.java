package com.example.denticaree;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AcceptedAppointmentsActivity extends AppCompatActivity {

    private ListView listViewAccepted;
    private FirebaseFirestore db;
    private String doctorUid;

    private List<AppointmentItem> acceptedAppointments;
    private AcceptedAppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_appointments);

        listViewAccepted = findViewById(R.id.listViewAccepted);
        db = FirebaseFirestore.getInstance();

        doctorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        acceptedAppointments = new ArrayList<>();
        adapter = new AcceptedAppointmentAdapter(this, acceptedAppointments, appointment -> {
            Intent intent = new Intent(AcceptedAppointmentsActivity.this, ConsultationActivity.class);
            intent.putExtra("appointmentId", appointment.getAppointmentId());
            intent.putExtra("patientId", appointment.getPatientId());
            intent.putExtra("doctorId", doctorUid);
            startActivity(intent);
        });

        listViewAccepted.setAdapter(adapter);

        loadAcceptedAppointments();
    }

    private void loadAcceptedAppointments() {
        db.collection("appointments")
                .whereEqualTo("doctorId", doctorUid)
                .whereEqualTo("status", "confirmed")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    acceptedAppointments.clear();
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

                                    acceptedAppointments.add(new AppointmentItem(appointmentId, patientId, fullName, date, time));
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Erreur récupération patient", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur chargement rendez-vous acceptés", Toast.LENGTH_SHORT).show());
    }
}
