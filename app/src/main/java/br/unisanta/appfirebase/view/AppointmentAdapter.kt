package br.unisanta.appfirebase.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.appfirebase.R
import br.unisanta.appfirebase.model.Appointment

class AppointmentAdapter(private val appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val patientName: TextView = view.findViewById(R.id.tv_patient_name)
        val details: TextView = view.findViewById(R.id.tv_appointment_details)
        val status: TextView = view.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.patientName.text = "Paciente: ${appointment.patientName}"
        holder.details.text = "Data: ${appointment.date} - Horário: ${appointment.time}"
        holder.status.text = "Status: ${appointment.status}"
    }

    override fun getItemCount() = appointments.size
}
