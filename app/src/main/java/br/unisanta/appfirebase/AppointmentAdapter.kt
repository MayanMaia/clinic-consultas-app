package br.unisanta.appfirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.appfirebase.model.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentAdapter(private val appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val patientName: TextView = view.findViewById(R.id.txv_patient_name)
        val appointmentDate: TextView = view.findViewById(R.id.txv_appointment_date)
        val appointmentStatus: TextView = view.findViewById(R.id.txv_appointment_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())

        holder.patientName.text = "Paciente: ${appointment.patientName}"
        holder.appointmentStatus.text = "Status: ${appointment.status}"

        appointment.date?.let {
            holder.appointmentDate.text = "Data: ${dateFormat.format(it)}"
        } ?: run {
            holder.appointmentDate.text = "Data: Não definida"
        }
    }

    override fun getItemCount() = appointments.size
}
