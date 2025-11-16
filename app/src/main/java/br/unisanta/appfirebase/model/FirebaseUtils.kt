package br.unisanta.appfirebase.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtils {
    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Constantes para as coleções do Firestore
    object Collections {
        const val USERS = "users"
        const val APPOINTMENTS = "appointments"
    }
}
