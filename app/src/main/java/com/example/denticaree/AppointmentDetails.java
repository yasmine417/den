package com.example.denticaree;

public class AppointmentDetails {
    private String date;
    private String time;
    private String motif;
    private String traitement;
    private String patientName;
    private String patientEmail;
    private String status;

    public AppointmentDetails(String date, String time, String motif, String traitement,
                              String patientName, String patientEmail, String status) {
        this.date = date;
        this.time = time;
        this.motif = motif;
        this.traitement = traitement;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.status = status;
    }

    // Getters
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getMotif() { return motif; }
    public String getTraitement() { return traitement; }
    public String getPatientName() { return patientName; }
    public String getPatientEmail() { return patientEmail; }
    public String getStatus() { return status; }
}

