package br.unisanta.appfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.appfirebase.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import androidx.activity.result.ActivityResultLauncher
import java.util.Arrays
import android.util.Log


class LoginEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginEmailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // Contrato para lidar com o resultado do login do FirebaseUI
    private val signInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            this.onSignInResult(res)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        binding.btnLoginEmail.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val senha = binding.edtSenhaLogin.text.toString()
            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Login com Sucesso!", Toast.LENGTH_SHORT).show()
                        // Verifica o perfil e redireciona
                        auth.currentUser?.uid?.let { checkUserProfileAndRedirect(it) }
                    }
                    else{
                        Toast.makeText(this, "Login Inválido: ${task.exception?.message}!", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 1. Configurar o botão de login com Google
        binding.btnGoogleLogin.setOnClickListener {
            createSignInIntent()
        }
    }

    // 2. Criar o Intent de Login do FirebaseUI
    private fun createSignInIntent() {
        // Escolha os provedores de autenticação que você deseja oferecer
        val providers = Arrays.asList(
            AuthUI.IdpConfig.GoogleBuilder().build()
            // Se quiser adicionar outros, descomente:
            // AuthUI.IdpConfig.EmailBuilder().build(),
            // AuthUI.IdpConfig.FacebookBuilder().build()
        )

        // Crie e lance o intent de login
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    // 3. Lidar com o resultado do login
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Login bem-sucedido
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "Login com Google bem-sucedido! Usuário: ${user?.displayName}", Toast.LENGTH_SHORT).show()
            // Verifica o perfil e redireciona
            val user = FirebaseAuth.getInstance().currentUser
            user?.uid?.let { checkUserProfileAndRedirect(it) }
        } else {
            // Login falhou
            if (response == null) {
                // O usuário cancelou o login
                Toast.makeText(this, "Login cancelado.", Toast.LENGTH_SHORT).show()
                return
            }
            if (response.error?.errorCode == com.firebase.ui.auth.ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "Erro: Sem conexão com a internet.", Toast.LENGTH_SHORT).show()
                return
            }
            // Outros erros
            Log.e("LoginEmailActivity", "Erro de login: ${response.error?.errorCode}", response.error)
            Toast.makeText(this, "Erro de login: ${response.error?.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkUserProfileAndRedirect(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val profile = document.getString("profile")
                val intent: Intent
                when (profile) {
                    "Paciente" -> intent = Intent(this, PatientHomeActivity::class.java)
                    "Médico" -> intent = Intent(this, DoctorHomeActivity::class.java)
                    else -> {
                        // Perfil não encontrado ou inválido, desloga por segurança
                        auth.signOut()
                        Toast.makeText(this, "Perfil de usuário inválido. Faça login novamente.", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao verificar perfil. Tente novamente.", Toast.LENGTH_LONG).show()
                auth.signOut()
            }
    }
}
