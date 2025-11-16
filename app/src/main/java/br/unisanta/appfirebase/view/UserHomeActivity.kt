package br.unisanta.appfirebase.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.unisanta.appfirebase.R
import br.unisanta.appfirebase.controller.AuthController
import br.unisanta.appfirebase.databinding.ActivityUserHomeBinding
import br.unisanta.appfirebase.model.User
import com.firebase.ui.auth.AuthUI

class UserHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserHomeBinding
    private val authController = AuthController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUserSession()

        setupButtons()
    }

    /**
     * Verifica se o usuário está logado (RN01).
     * Caso contrário, redireciona para o login.
     */
    private fun checkUserSession() {
        val currentUser = authController.getCurrentUser()

        if (currentUser == null) {
            goToLogin()
            return
        }

        // Carrega o perfil do Firestore (RN02)
        loadUserProfile(currentUser.uid)
    }

    /**
     * Configuração dos botões: agendamento, consulta e logout.
     */
    private fun setupButtons() {

        // RF02 — Agendar consulta
        binding.btnScheduleAppointment.setOnClickListener {
            startActivity(Intent(this, ScheduleAppointmentActivity::class.java))
        }

        // RF03 — Visualizar consultas
        binding.btnViewAppointments.setOnClickListener {
            startActivity(Intent(this, ViewAppointmentsActivity::class.java))
        }

        // Botão Sair
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    /**
     * Obtém o perfil e exibe o nome e permissões do usuário.
     */
    private fun loadUserProfile(uid: String) {
        authController.getUserProfile(
            uid,
            onSuccess = { user ->
                showWelcomeMessage(user)
                configurePermissions(user)
            },
            onFailure = {
                showWelcomeMessage(User(email = "Desconhecido", profile = "Paciente"))
                configurePermissions(User(profile = "Paciente"))
                Toast.makeText(this, "Erro ao carregar perfil.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    /**
     * Atualiza a mensagem de boas-vindas.
     */
    private fun showWelcomeMessage(user: User) {
        binding.tvWelcome.text = "Bem-vindo(a), ${user.email} (${user.profile})"
    }

    /**
     * RN02 — Permissões por perfil.
     * Paciente → Agendar consulta
     * Médico → Visualizar consultas
     */
    private fun configurePermissions(user: User) {
        when (user.profile) {
            "Paciente" -> {
                binding.btnScheduleAppointment.visibility = View.VISIBLE
                binding.btnViewAppointments.visibility = View.GONE
            }
            "Médico" -> {
                binding.btnScheduleAppointment.visibility = View.GONE
                binding.btnViewAppointments.visibility = View.VISIBLE
            }
            else -> {
                binding.btnScheduleAppointment.visibility = View.GONE
                binding.btnViewAppointments.visibility = View.GONE
            }
        }
    }

    /**
     * Finaliza sessão com FirebaseUI e retorna ao Login.
     */
    private fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(this, "Sessão encerrada.", Toast.LENGTH_SHORT).show()
                goToLogin()
            }
    }

    /**
     * Redireciona para a tela de login limpando o histórico.
     */
    private fun goToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
