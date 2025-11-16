package br.unisanta.appfirebase.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.unisanta.appfirebase.controller.AppointmentController
import br.unisanta.appfirebase.databinding.ActivityViewAppointmentsBinding
import br.unisanta.appfirebase.model.Appointment
import br.unisanta.appfirebase.R
class ViewAppointmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAppointmentsBinding
    private val appointmentController = AppointmentController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvAppointments.layoutManager = LinearLayoutManager(this)

        loadAppointments()
    }

    private fun loadAppointments() {
        // RF03: Médicos devem poder visualizar uma lista de consultas agendadas
        appointmentController.getScheduledAppointments(
            onSuccess = { appointments ->
                if (appointments.isEmpty()) {
                    binding.rvAppointments.visibility = View.GONE
                    binding.tvNoAppointments.visibility = View.VISIBLE
                } else {
                    binding.rvAppointments.visibility = View.VISIBLE
                    binding.tvNoAppointments.visibility = View.GONE
                    binding.rvAppointments.adapter = AppointmentAdapter(appointments)
                }
            },
            onFailure = { e ->
                Toast.makeText(this, "Erro ao carregar consultas: ${e.message}", Toast.LENGTH_LONG).show()
                binding.rvAppointments.visibility = View.GONE
                binding.tvNoAppointments.visibility = View.VISIBLE
                binding.tvNoAppointments.text = "Erro: ${e.message}"
            }
        )
    }
}
