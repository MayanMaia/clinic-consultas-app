package br.unisanta.appfirebase.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.appfirebase.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import br.unisanta.appfirebase.controller.AuthController
import br.unisanta.appfirebase.view.UserHomeActivity
import br.unisanta.appfirebase.R

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authController: AuthController
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

        authController = AuthController()

        binding.btnRegisterAccount.setOnClickListener {
            val email = binding.edtEmailRegister.text.toString()
            val senha = binding.edtSenhaRegister.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lógica para obter o perfil selecionado (RN02)
            val selectedProfileId = binding.rgProfileSelection.checkedRadioButtonId
            val profile = if (selectedProfileId == binding.rbDoctor.id) "Médico" else "Paciente"

            // Lógica para criar novo usuário no Firebase usando o Controller
            authController.register(email, senha, profile,
                onSuccess = { user ->
                    // Cadastro bem-sucedido, redireciona para a tela inicial do usuário
                    Toast.makeText(this, "Cadastro realizado com sucesso! Perfil: ${user.profile}", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, UserHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { exception ->
                    // Se o cadastro falhar, exibe uma mensagem ao usuário.
                    Toast.makeText(this, "Falha no cadastro: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}
