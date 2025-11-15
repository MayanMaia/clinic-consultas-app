package br.unisanta.appfirebase.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Appointment(
    @DocumentId
    val id: String = "",
    val patientUid: String = "",
    val patientName: String = "",
    val doctorUid: String = "", // Para o desafio, pode ser um valor fixo ou um placeholder
    val doctorName: String = "Dr. Manus", // Placeholder para o desafio
    val date: Date? = null, // Data e hora do agendamento
    val status: String = "Agendada", // e.g., "Agendada", "Cancelada", "Realizada"
    @ServerTimestamp
    val createdAt: Date? = null // Timestamp de criação no servidor
)
