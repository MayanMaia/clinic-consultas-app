package br.unisanta.appfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.unisanta.appfirebase.databinding.ActivityDoctorHomeBinding
import br.unisanta.appfirebase.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DoctorHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: AppointmentAdapter
    private val appointmentsList = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        setupRecyclerView()
        loadDoctorData()
        loadAppointments()

        binding.btnLogoutDoctor.setOnClickListener { logout() }
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(appointmentsList)
        binding.rvAppointments.layoutManager = LinearLayoutManager(this)
        binding.rvAppointments.adapter = adapter
    }

    private fun loadDoctorData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "Doutor(a)"
                binding.txvWelcomeDoctor.text = "Bem-vindo(a), $name!"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar dados do médico.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadAppointments() {
        // Para o desafio, vamos carregar todas as consultas. Em um cenário real, filtraríamos pelo doctorUid.
        firestore.collection("appointments")
            .orderBy("date")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Erro ao carregar consultas: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    appointmentsList.clear()
                    for (doc in snapshots.documents) {
                        val appointment = doc.toObject(Appointment::class.java)
                        if (appointment != null) {
                            appointmentsList.add(appointment)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
