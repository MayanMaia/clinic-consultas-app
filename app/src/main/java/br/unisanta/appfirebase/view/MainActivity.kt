package br.unisanta.appfirebase.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import br.unisanta.appfirebase.databinding.ActivityMainBinding // Certifique-se que você tem o activity_main.xml
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import br.unisanta.appfirebase.R
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    // Cria o "Lançador" da FirebaseUI
    private val signInLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result) // Função que trata a resposta
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 1. Verificar se o usuário JÁ ESTÁ LOGADO
        if (auth.currentUser != null) {
            goToUserHome()
            return
        }

        // 2. Botão que lança a tela da FirebaseUI (como na imagem)
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginEmailActivity::class.java)
            startActivity(intent)
        }

        // Botão para criar nova conta
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Função que CRIA e LANÇA a tela da FirebaseUI
    private fun launchFirebaseUI() {
        // Habilite os provedores que você quer (e ative-os no Console do Firebase!)
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            // --- CORREÇÃO 1: REMOVIDO setIsNewUser, pois não existe aqui. ---
            // O FirebaseUI gerencia isso automaticamente.

            // --- Personalização (opcional, mas recomendado) ---
            .setLogo(R.drawable.ic_launcher_foreground) // Use a sua logo
            .build()

        signInLauncher.launch(signInIntent)
    }

    // Função que TRATA O RESULTADO (Sucesso, Falha ou Erro)
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == RESULT_OK) {
            // Login ou Cadastro BEM SUCEDIDO!
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "Sucesso! Bem-vindo(a), ${user?.email}", Toast.LENGTH_LONG).show()
            goToUserHome()
        } else {
            // Login ou Cadastro FALHOU
            if (response == null) {
                // Usuário cancelou (apertou o botão 'voltar')
                Toast.makeText(this, "Login cancelado.", Toast.LENGTH_SHORT).show()
            } else {
                // Erro (senha errada, sem rede, etc.)
                // --- CORREÇÃO 2: Alterado LONG_LONG para LENGTH_LONG ---
                Toast.makeText(this, "Erro: ${response.error?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Função para navegar para a tela logada
    private fun goToUserHome() {
        val intent = Intent(this, UserHomeActivity::class.java)
        startActivity(intent)
        finish() // Fecha a MainActivity
    }
}