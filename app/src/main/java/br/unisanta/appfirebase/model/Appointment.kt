package br.unisanta.appfirebase.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Appointment(
    @DocumentId
    val id: String = "",
    val patientUid: String = "",
    val patientName: String = "",
    val doctorUid: String = "", // Para futura expansão, mas por enquanto pode ser um valor fixo ou null
    val doctorName: String = "Dr. Exemplo", // Nome do médico que atenderá
    val date: String = "", // Data da consulta (ex: "2025-12-25")
    val time: String = "", // Horário da consulta (ex: "10:00")
    val status: String = "Agendada", // Status da consulta (Agendada, Cancelada, Realizada)
    @ServerTimestamp
    val createdAt: Date? = null
) {
    // Construtor vazio necessário para o Firestore
    constructor() : this("", "", "", "", "", "", "", "Agendada", null)
}
