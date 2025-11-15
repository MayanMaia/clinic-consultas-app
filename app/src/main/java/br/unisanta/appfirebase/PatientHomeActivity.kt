package br.unisanta.appfirebase

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.unisanta.appfirebase.databinding.ActivityPatientHomeBinding
import br.unisanta.appfirebase.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date

class PatientHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var selectedDate: Date? = null
    private var patientName: String = "Paciente" // Será atualizado com o nome real

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        loadPatientData()

        binding.edtDate.setOnClickListener { showDatePicker() }
        binding.edtTime.setOnClickListener { showTimePicker() }
        binding.btnSchedule.setOnClickListener { scheduleAppointment() }
        binding.btnLogoutPatient.setOnClickListener { logout() }
    }

    private fun loadPatientData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "Paciente"
                patientName = name
                binding.txvWelcomePatient.text = "Bem-vindo, $name!"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar dados do paciente.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = calendar.time
            binding.edtDate.setText(String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear))
        }, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // Não permite datas passadas
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            if (selectedDate != null) {
                val dateCalendar = Calendar.getInstance().apply { time = selectedDate!! }
                dateCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                dateCalendar.set(Calendar.MINUTE, selectedMinute)
                dateCalendar.set(Calendar.SECOND, 0)
                dateCalendar.set(Calendar.MILLISECOND, 0)

                selectedDate = dateCalendar.time
                binding.edtTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            } else {
                Toast.makeText(this, "Selecione a data primeiro.", Toast.LENGTH_SHORT).show()
            }
        }, hour, minute, true) // true para formato 24h
        timePickerDialog.show()
    }

    private fun scheduleAppointment() {
        val doctorName = binding.edtDoctorName.text.toString().trim()

        if (doctorName.isEmpty() || selectedDate == null) {
            Toast.makeText(this, "Preencha o nome do médico, data e hora.", Toast.LENGTH_LONG).show()
            return
        }

        val appointment = Appointment(
            patientUid = auth.currentUser!!.uid,
            patientName = patientName,
            doctorName = doctorName,
            date = selectedDate
        )

        firestore.collection("appointments").add(appointment)
            .addOnSuccessListener {
                Toast.makeText(this, "Consulta agendada com sucesso!", Toast.LENGTH_LONG).show()
                // Limpar campos
                binding.edtDoctorName.setText("")
                binding.edtDate.setText("")
                binding.edtTime.setText("")
                selectedDate = null
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao agendar consulta: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
