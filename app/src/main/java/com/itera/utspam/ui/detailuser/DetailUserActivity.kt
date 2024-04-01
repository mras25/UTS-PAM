package com.itera.utspam.ui.detailuser

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.itera.utspam.R
import com.itera.utspam.data.model.DataItem
import com.itera.utspam.data.model.SingleUserData
import com.itera.utspam.data.model.SingleUserResponse
import com.itera.utspam.data.model.UserResponse
import com.itera.utspam.data.source.remote.retrofit.ApiConfig
import com.itera.utspam.databinding.ActivityDetailUserBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        getUser()


    }

    private fun getUser() {
        val client = ApiConfig.getApiService().getUser(intent.getIntExtra(EXTRA_ID, 1).toString())

        client.enqueue(object : Callback<SingleUserResponse> {
            override fun onResponse(call: Call<SingleUserResponse>, response: Response<SingleUserResponse>) {

                if (response.isSuccessful) {
                    val data = response.body()?.data as SingleUserData

                    binding.apply {
                        tvEmail.text = data.email
                        tvName.text = "${data.firstName} ${data.lastName}"

                        Glide.with(this@DetailUserActivity)
                            .load(data.avatar)
                            .apply(RequestOptions().override(80, 80).placeholder(R.drawable.baseline_person_24))
                            .transform(CircleCrop())
                            .into(ivImageProfile)
                    }
                }
            }

            override fun onFailure(call: Call<SingleUserResponse>, t: Throwable) {
                Toast.makeText(this@DetailUserActivity, t.message, Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }

    companion object {
        const val EXTRA_ID = "extraId"
    }
}