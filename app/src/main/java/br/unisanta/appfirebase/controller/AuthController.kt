package br.unisanta.appfirebase.controller

import br.unisanta.appfirebase.model.FirebaseUtils
import br.unisanta.appfirebase.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthController(
    private val auth: FirebaseAuth = FirebaseUtils.auth,
    private val firestore: FirebaseFirestore = FirebaseUtils.firestore
) {

    fun getCurrentUser() = auth.currentUser

    fun saveUserProfile(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (user.uid.isEmpty()) {
            onFailure(IllegalStateException("UID do usuário não pode ser vazio."))
            return
        }

        firestore.collection(FirebaseUtils.Collections.USERS)
            .document(user.uid)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getUserProfile(uid: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(FirebaseUtils.Collections.USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) onSuccess(user)
                else onFailure(NoSuchElementException("Perfil do usuário não encontrado."))
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun signIn(email: String, password: String, onSuccess: (AuthResult) -> Unit, onFailure: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) task.result?.let { onSuccess(it) }
                else task.exception?.let { onFailure(it) }
            }
    }

    fun register(email: String, password: String, profile: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    if (firebaseUser != null) {

                        val newUser = User(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            profile = profile
                        )

                        saveUserProfile(newUser,
                            onSuccess = { onSuccess(newUser) },
                            onFailure = { e ->
                                auth.signOut()
                                onFailure(e)
                            }
                        )

                    } else {
                        onFailure(IllegalStateException("Usuário nulo após cadastro."))
                    }
                } else {
                    task.exception?.let { onFailure(it) }
                }
            }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun getCurrentUserUid(): String? = auth.currentUser?.uid

    fun signOut() = auth.signOut()
}
