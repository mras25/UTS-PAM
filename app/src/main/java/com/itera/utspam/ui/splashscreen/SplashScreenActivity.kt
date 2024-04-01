package com.itera.utspam.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.asLiveData
import com.itera.utspam.R
import com.itera.utspam.data.source.local.UserPreferences
import com.itera.utspam.data.source.local.datastore
import com.itera.utspam.databinding.ActivitySplashScreenBinding
import com.itera.utspam.ui.login.LoginActivity
import com.itera.utspam.ui.main.MainActivity
import com.itera.utspam.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.runBlocking

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            ivLogo.alpha = 0f

            ivLogo.animate().setDuration(3000).alpha(1f).withEndAction {

                val userPreferences =
                    UserPreferences.getInstance(this@SplashScreenActivity.datastore)

                runBlocking {
                    userPreferences.getSession().asLiveData()
                        .observe(this@SplashScreenActivity) { user ->
                            if (user.name != "") {
                                Log.d("USER", user.email)
                                Intent(this@SplashScreenActivity, MainActivity::class.java).also {
                                    startActivity(it)
                                    finishAffinity()
                                }
                            } else {
                                Intent(this@SplashScreenActivity, OnboardingActivity::class.java).also {
                                    startActivity(it)
                                }
                            }
                        }
                }


            }
        }
    }
}