package com.example.denticaree;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

public class BookingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView dateTextView, timeTextView;
    private Spinner doctorSpinner;
    private Button saveButton;
    private Button listDoctorsButton;
    private Button historique;
    private ListView disponibilitesListView;

    private String selectedDoctorId;

    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private String patientUid;

    private List<Date> availableSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("BookingActivity", "Utilisateur non connecté, redirection vers MainActivity");
            Toast.makeText(this, "Session expirée, veuillez vous reconnecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        patientUid = currentUser.getUid();
        Log.d("BookingActivity", "Utilisateur connecté : " + patientUid);
        setContentView(R.layout.activity_booking);

        db = FirebaseFirestore.getInstance();

        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        doctorSpinner = findViewById(R.id.doctorSpinner);
        saveButton = findViewById(R.id.saveButton);
        listDoctorsButton = findViewById(R.id.listDoctorsButton);
        historique = findViewById(R.id.historique);
        disponibilitesListView = findViewById(R.id.disponibilitesListView);

        loadDoctors();

        dateTextView.setOnClickListener(v -> showDatePicker());
        timeTextView.setOnClickListener(v -> showTimePicker());

        saveButton.setOnClickListener(v -> {
            saveButton.setEnabled(false);
            saveAppointment();
        });

        listDoctorsButton.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, DoctorsActivity.class);
            startActivity(intent);
        });

        historique.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, MyAppointmentsActivity.class);
            startActivity(intent);
        });
    }

    private void loadDoctors() {
        Log.d("BookingActivity", "Chargement des médecins...");
        db.collection("users")
                .whereEqualTo("role", "doctor")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> doctorNames = new ArrayList<>();
                    List<String> doctorIds = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("nom");
                        if (name != null && !name.isEmpty()) {
                            doctorNames.add(name);
                            doctorIds.add(doc.getId());
                        }
                    }

                    if (doctorNames.isEmpty()) {
                        Toast.makeText(this, "Aucun médecin disponible", Toast.LENGTH_LONG).show();
                        return;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctorNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    doctorSpinner.setAdapter(adapter);

                    doctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedDoctorId = doctorIds.get(position);
                            loadDisponibilitesForDoctor(selectedDoctorId);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedDoctorId = null;
                            availableSlots.clear();
                            disponibilitesListView.setAdapter(null);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("BookingActivity", "Erreur lors du chargement des médecins : " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur de chargement des médecins : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadDisponibilitesForDoctor(String doctorId) {
        db.collection("availabilities")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .addOnSuccessListener(query -> {
                    availableSlots.clear();
                    List<String> affichage = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Date slot = doc.getDate("timestamp");
                        if (slot != null) {
                            availableSlots.add(slot);
                            affichage.add(android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", slot).toString());
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, affichage);
                    disponibilitesListView.setAdapter(adapter);

                    disponibilitesListView.setOnItemClickListener((parent, view, position, id) -> {
                        Date selectedSlot = availableSlots.get(position);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(selectedSlot);

                        selectedYear = cal.get(Calendar.YEAR);
                        selectedMonth = cal.get(Calendar.MONTH);
                        selectedDay = cal.get(Calendar.DAY_OF_MONTH);
                        selectedHour = cal.get(Calendar.HOUR_OF_DAY);
                        selectedMinute = cal.get(Calendar.MINUTE);

                        dateTextView.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
                        timeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur de chargement des disponibilités", Toast.LENGTH_SHORT).show();
                    Log.e("BookingActivity", "loadDisponibilitesForDoctor: ", e);
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = dayOfMonth;
            dateTextView.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedHour = hourOfDay;
            selectedMinute = minute;
            timeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void saveAppointment() {
        String selectedDate = dateTextView.getText().toString().trim();
        String selectedTime = timeTextView.getText().toString().trim();

        if (selectedDoctorId == null || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
        Date selectedTimestamp = calendar.getTime();

        // Vérifie que ce créneau n'est pas déjà pris (pour ce médecin)
        db.collection("appointments")
                .whereEqualTo("doctorId", selectedDoctorId)
                .whereEqualTo("timestamp", selectedTimestamp)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        Toast.makeText(this, "Ce créneau est déjà réservé pour ce médecin", Toast.LENGTH_LONG).show();
                        saveButton.setEnabled(true);
                    } else {
                        Map<String, Object> appointment = new HashMap<>();
                        appointment.put("patientId", patientUid);
                        appointment.put("doctorId", selectedDoctorId);
                        appointment.put("date", selectedDate);
                        appointment.put("time", selectedTime);
                        appointment.put("timestamp", selectedTimestamp);
                        appointment.put("status", "pending");

                        db.collection("appointments")
                                .add(appointment)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(this, "Rendez-vous réservé avec succès", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Échec de la réservation", Toast.LENGTH_SHORT).show();
                                    Log.e("Firestore", "Erreur", e);
                                    saveButton.setEnabled(true);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur lors de la vérification des disponibilités", Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
                });
    }
}
