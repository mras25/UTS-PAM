package com.itera.utspam.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.itera.utspam.R
import com.itera.utspam.data.model.LocalUser
import com.itera.utspam.data.source.local.UserPreferences
import com.itera.utspam.data.source.local.datastore
import com.itera.utspam.databinding.ActivityRegisterBinding
import com.itera.utspam.ui.main.MainActivity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            btnLogin.setOnClickListener {
                finish()
            }

            btnDaftar.setOnClickListener {
                registerAccount()
            }
        }
    }

    private fun registerAccount() {
        var valid = true

        binding.apply {
            val name = edRegisterNama.text.toString()
            val email = edRegisterEmail.text.toString()
            val usernameGithub = edRegisterGithub.text.toString()
            val nik = edRegisterNik.text.toString()
            val password = edRegisterPassword.text.toString()

            if (name.isEmpty()) {
                valid = false
                edRegisterNama.error = "Nama tidak boleh kosong"
            }

            if (email.isEmpty()) {
                valid = false
                edRegisterEmail.error = "Email tidak boleh kosong"
            }

            if (usernameGithub.isEmpty()) {
                valid = false
                edRegisterGithub.error = "Username Github tidak boleh kosong"
            }

            if (nik.isEmpty()) {
                valid = false
                edRegisterNik.error = "NIK tidak boleh kosong"
            }

            if (password.isEmpty()) {
                valid = false
                edRegisterPassword.error = "Password tidak boleh kosong"
            }

            if (valid) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Snackbar.make(
                                binding.root,
                                "Berhasil mendaftar",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            val newUser = task.result.user

                            val firestoreDB = Firebase.firestore

                            val prepareUser = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "githubUsername" to usernameGithub,
                                "nik" to nik
                            )

                            firestoreDB.collection("users").document(newUser?.uid ?: "")
                                .set(prepareUser).addOnSuccessListener {
                                    val userPreferences = UserPreferences.getInstance(this@RegisterActivity.datastore)

                                    val user = LocalUser(
                                        name = name,
                                        email = email,
                                        githubUsername = usernameGithub
                                    )

                                    runBlocking {
                                        userPreferences.saveSession(user)

                                        Intent(this@RegisterActivity, MainActivity::class.java).also {
                                            startActivity(it)
                                        }
                                        finishAffinity()
                                    }


                                }.addOnFailureListener {
                                    Snackbar.make(
                                        binding.root,
                                        it.message.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }

                        } else {
                            Snackbar.make(
                                binding.root,
                                task.exception?.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }

            }

        }
    }
}