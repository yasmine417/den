package com.example.denticaree;

public class AppointmentItem {
    public String appointmentId;
    public String patientId;
    public String patientName;
    public String date;
    public String time;

    public AppointmentItem(String appointmentId, String patientId, String patientName, String date, String time) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.date = date;
        this.time = time;
    }
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}
