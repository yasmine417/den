package com.example.denticaree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PastAppointmentAdapter extends BaseAdapter {

    private Context context;
    private List<AppointmentDetails> appointments;
    private LayoutInflater inflater;

    public PastAppointmentAdapter(Context context, List<AppointmentDetails> appointments) {
        this.context = context;
        this.appointments = appointments;
        inflater = LayoutInflater.from(context);
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

    static class ViewHolder {
        TextView tvDate, tvTime, tvMotif, tvTraitement, tvPatientName, tvPatientEmail, tvStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.appointment_detail_item, parent, false);
            holder = new ViewHolder();
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            holder.tvTime = convertView.findViewById(R.id.tvTime);
            holder.tvMotif = convertView.findViewById(R.id.tvMotif);
            holder.tvTraitement = convertView.findViewById(R.id.tvTraitement);
            holder.tvPatientName = convertView.findViewById(R.id.tvPatientName);
            holder.tvPatientEmail = convertView.findViewById(R.id.tvPatientEmail);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppointmentDetails appointment = appointments.get(position);

        holder.tvDate.setText("Date : " + safe(appointment.getDate()));
        holder.tvTime.setText("Heure : " + safe(appointment.getTime()));
        holder.tvMotif.setText("Motif : " + safe(appointment.getMotif()));
        holder.tvTraitement.setText("Traitement : " + safe(appointment.getTraitement()));
        holder.tvPatientName.setText("Nom : " + safe(appointment.getPatientName()));
        holder.tvPatientEmail.setText("Email : " + safe(appointment.getPatientEmail()));
        holder.tvStatus.setText("Statut : " + safe(appointment.getStatus()));

        return convertView;
    }

    private String safe(String s) {
        return s != null ? s : "Non renseigné";
    }

}
