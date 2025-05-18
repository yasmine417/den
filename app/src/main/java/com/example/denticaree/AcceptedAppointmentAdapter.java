// AcceptedAppointmentAdapter.java

package com.example.denticaree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class AcceptedAppointmentAdapter extends BaseAdapter {

    private final List<AppointmentItem> appointments;
    private final Context context;
    private final OnConsultClickListener listener;

    public interface OnConsultClickListener {
        void onConsultClick(AppointmentItem appointment);
    }

    public AcceptedAppointmentAdapter(Context context, List<AppointmentItem> appointments, OnConsultClickListener listener) {
        this.context = context;
        this.appointments = appointments;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Object getItem(int position) {
        return appointments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_accepted_appointment, parent, false);
        }

        TextView tvPatientName = convertView.findViewById(R.id.tvPatientName);
        TextView tvDateTime = convertView.findViewById(R.id.tvDateTime);
        Button btnConsult = convertView.findViewById(R.id.btnConsult);

        AppointmentItem appointment = appointments.get(position);

        tvPatientName.setText(appointment.getPatientName());
        tvDateTime.setText(appointment.getDate() + " à " + appointment.getTime());

        btnConsult.setOnClickListener(v -> listener.onConsultClick(appointment));

        return convertView;
    }
}
