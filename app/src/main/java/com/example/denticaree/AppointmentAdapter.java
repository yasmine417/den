package com.example.denticaree;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<AppointmentItem> {

    private final AppointmentActionCallback callback;

    public interface AppointmentActionCallback {
        void onStatusChange(String appointmentId, String patientId, String newStatus);
    }

    public AppointmentAdapter(Activity context, List<AppointmentItem> appointments, AppointmentActionCallback callback) {
        super(context, 0, appointments);
        this.callback = callback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppointmentItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_appointment, parent, false);
        }

        TextView patientNameText = convertView.findViewById(R.id.patientNameText);
        TextView dateTimeText = convertView.findViewById(R.id.dateTimeText);
        Button confirmButton = convertView.findViewById(R.id.confirmButton);
        Button cancelButton = convertView.findViewById(R.id.cancelButton);

        patientNameText.setText("Patient: " + item.patientName);
        dateTimeText.setText("Date: " + item.date + " à " + item.time);

        confirmButton.setOnClickListener(v ->
                callback.onStatusChange(item.appointmentId, item.patientId, "confirmed"));

        cancelButton.setOnClickListener(v ->
                callback.onStatusChange(item.appointmentId, item.patientId, "cancelled"));

        return convertView;
    }
}
