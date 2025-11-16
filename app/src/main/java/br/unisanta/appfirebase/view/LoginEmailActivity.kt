package br.unisanta.appfirebase.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.appfirebase.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth
import br.unisanta.appfirebase.controller.AuthController
import br.unisanta.appfirebase.view.UserHomeActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import androidx.activity.result.ActivityResultLauncher
import java.util.Arrays
import android.util.Log
import br.unisanta.appfirebase.R

class LoginEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginEmailBinding
    private lateinit var authController: AuthController

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

        authController = AuthController()

        binding.btnLoginEmail.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val senha = binding.edtSenhaLogin.text.toString()
            authController.signIn(email, senha,
                onSuccess = {
                    Toast.makeText(this, "Login com Sucesso!", Toast.LENGTH_SHORT).show()
                    // Redirecionar para a tela inicial do usuário
                    val intent = Intent(this, UserHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, "Login Inválido: ${exception.message}!", Toast.LENGTH_LONG).show()
                }
            )
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
            // Redirecionar para a tela inicial do usuário
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
            finish()
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
}
