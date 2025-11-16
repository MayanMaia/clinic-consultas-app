package br.unisanta.appfirebase.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    // RN02: Campo para identificar o perfil do usuário
    val profile: String = "Paciente" // Pode ser "Paciente" ou "Médico"
) {
    // Construtor vazio necessário para o Firestore
    constructor() : this("", "", "", "Paciente")
}
