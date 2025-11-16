package br.unisanta.appfirebase.controller

import br.unisanta.appfirebase.model.Appointment
import br.unisanta.appfirebase.model.FirebaseUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AppointmentController(
    private val firestore: FirebaseFirestore = FirebaseUtils.firestore
) {

    /**
     * RF02: Permite que pacientes agendem consultas.
     * @param appointment O objeto Appointment a ser salvo.
     * @param onSuccess Callback de sucesso.
     * @param onFailure Callback de falha.
     */
    fun scheduleAppointment(appointment: Appointment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Adiciona a consulta à coleção 'appointments'
        firestore.collection(FirebaseUtils.Collections.APPOINTMENTS)
            .add(appointment)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * RF03: Permite que médicos visualizem uma lista de consultas agendadas.
     * @param onSuccess Callback de sucesso com a lista de consultas.
     * @param onFailure Callback de falha.
     */
    fun getScheduledAppointments(onSuccess: (List<Appointment>) -> Unit, onFailure: (Exception) -> Unit) {
        // Busca todas as consultas, ordenadas por data e hora
        firestore.collection(FirebaseUtils.Collections.APPOINTMENTS)
            .orderBy("date", Query.Direction.ASCENDING)
            .orderBy("time", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val appointments = result.toObjects(Appointment::class.java)
                onSuccess(appointments)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * Busca as consultas agendadas por um paciente específico.
     * @param patientUid O UID do paciente.
     * @param onSuccess Callback de sucesso com a lista de consultas.
     * @param onFailure Callback de falha.
     */
    fun getPatientAppointments(patientUid: String, onSuccess: (List<Appointment>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(FirebaseUtils.Collections.APPOINTMENTS)
            .whereEqualTo("patientUid", patientUid)
            .orderBy("date", Query.Direction.ASCENDING)
            .orderBy("time", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val appointments = result.toObjects(Appointment::class.java)
                onSuccess(appointments)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
