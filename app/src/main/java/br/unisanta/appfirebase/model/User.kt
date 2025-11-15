package br.unisanta.appfirebase.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val profile: String = "Paciente" // "Paciente" ou "Médico"
)
