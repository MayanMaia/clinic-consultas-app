package br.unisanta.appfirebase.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.unisanta.appfirebase.controller.AppointmentController
import br.unisanta.appfirebase.controller.AuthController
import br.unisanta.appfirebase.databinding.ActivityScheduleAppointmentBinding
import br.unisanta.appfirebase.model.Appointment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import br.unisanta.appfirebase.R
class ScheduleAppointmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleAppointmentBinding
    private val appointmentController = AppointmentController()
    private val authController = AuthController()
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura os listeners para seleção de data e hora
        binding.etDate.setOnClickListener { showDatePicker() }
        binding.etTime.setOnClickListener { showTimePicker() }
        binding.btnSchedule.setOnClickListener { scheduleAppointment() }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // Impede agendamento no passado
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.etTime.setText(timeFormat.format(calendar.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Formato 24h
        )
        timePickerDialog.show()
    }

    private fun scheduleAppointment() {
        val date = binding.etDate.text.toString()
        val time = binding.etTime.text.toString()
        val patientUid = authController.getCurrentUserUid()

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione a data e o horário.", Toast.LENGTH_SHORT).show()
            return
        }

        if (patientUid == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_LONG).show()
            return
        }

        // Busca o nome do paciente no AuthController antes de agendar
        authController.getUserProfile(patientUid,
            onSuccess = { user ->
                val appointment = Appointment(
                    patientUid = patientUid,
                    patientName = user.name.ifEmpty { user.email }, // Usa o nome ou e-mail
                    date = date,
                    time = time
                )

                // RF02: Agendar consulta
                appointmentController.scheduleAppointment(appointment,
                    onSuccess = {
                        Toast.makeText(this, "Consulta agendada com sucesso para ${date} às ${time}!", Toast.LENGTH_LONG).show()
                        finish()
                    },
                    onFailure = { e ->
                        Toast.makeText(this, "Falha ao agendar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                )
            },
            onFailure = { e ->
                Toast.makeText(this, "Erro ao buscar perfil do paciente: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}
