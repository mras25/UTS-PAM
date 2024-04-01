package com.itera.utspam.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.itera.utspam.R
import com.itera.utspam.data.model.LocalUser
import com.itera.utspam.data.source.local.UserPreferences
import com.itera.utspam.data.source.local.datastore
import com.itera.utspam.databinding.ActivityLoginBinding
import com.itera.utspam.ui.main.MainActivity
import com.itera.utspam.ui.register.RegisterActivity
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            btnLogin.setOnClickListener {
                login()
            }

            btnDaftar.setOnClickListener {
                Intent(this@LoginActivity, RegisterActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun login() {
        binding.apply {
            val email = edLoginEmail.text.toString()
            val password = edLoginPassword.text.toString()

            when {
                TextUtils.isEmpty(email) -> {
                    edLoginEmail.error = "Email tidak boleh kosong"
                }

                TextUtils.isEmpty(password) -> {
                    edLoginPassword.error = "Password tidak boleh kosong"
                }

                else -> {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firestoreDb = Firebase.firestore

                                firestoreDb.collection("users")
                                    .document(FirebaseAuth.getInstance().uid.toString()).get().addOnSuccessListener { document ->
                                        if (document != null) {
                                            runBlocking {
                                                val userPreferences = UserPreferences.getInstance(this@LoginActivity.datastore)

                                                val user = LocalUser(
                                                    name = document.data?.getValue("name").toString(),
                                                    email = document.data?.getValue("email").toString(),
                                                    githubUsername = document.data?.getValue("githubUsername").toString()
                                                )

                                                runBlocking {
                                                    userPreferences.saveSession(user)

                                                    Snackbar.make(
                                                        binding.root,
                                                        "Berhasil masuk",
                                                        Snackbar.LENGTH_SHORT
                                                    ).show()

                                                    Intent(this@LoginActivity, MainActivity::class.java).also {
                                                        startActivity(it)
                                                    }
                                                    finishAffinity()
                                                }
                                            }
                                        }
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
}