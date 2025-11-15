package br.unisanta.appfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.appfirebase.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import br.unisanta.appfirebase.model.User


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // O nome do binding é gerado automaticamente a partir do nome do layout: activity_register -> ActivityRegisterBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        binding.btnRegisterAccount.setOnClickListener {
            val name = binding.edtNameRegister.text.toString()
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtSenhaRegister.text.toString()
            val profileId = binding.radioGroupProfile.checkedRadioButtonId
            val profile = if (profileId == binding.radioPatient.id) "Paciente" else "Médico"

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lógica para criar novo usuário no Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val user = User(
                            uid = firebaseUser!!.uid,
                            email = email,
                            name = name,
                            profile = profile
                        )

                        firestore.collection("users").document(user.uid).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(baseContext, "Conta criada e perfil salvo.", Toast.LENGTH_SHORT).show()
                                // Redireciona para a tela principal, que fará a verificação de perfil
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(baseContext, "Falha ao salvar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                                // Opcional: Deletar o usuário do Auth se falhar no Firestore
                                firebaseUser?.delete()
                            }
                    } else {
                        // Se o cadastro falhar, exibe uma mensagem ao usuário.
                        Toast.makeText(this, "Falha no cadastro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
