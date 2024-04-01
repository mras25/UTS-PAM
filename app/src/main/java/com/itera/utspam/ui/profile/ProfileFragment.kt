package com.itera.utspam.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.itera.utspam.R
import com.itera.utspam.data.source.local.UserPreferences
import com.itera.utspam.data.source.local.datastore
import com.itera.utspam.databinding.FragmentProfileBinding
import com.itera.utspam.ui.login.LoginActivity
import com.itera.utspam.ui.main.MainActivity
import kotlinx.coroutines.runBlocking


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences.getInstance(requireContext().datastore)

        runBlocking {
            userPreferences.getSession().asLiveData().observe(viewLifecycleOwner) { user ->
                if (user !=  null) {
                    binding.apply {
                        tvNameInitial.text = user.name.uppercase().get(0).toString()
                        tvName.text = user.name
                        tvGithubUsername.text = user.githubUsername
                        tvEmail.text = user.email
                    }
                }
            }
        }

        binding.btnLogout.setOnClickListener {

            runBlocking {
                userPreferences.logout()
                FirebaseAuth.getInstance().signOut()

                Intent(requireActivity(), LoginActivity::class.java).also {
                    startActivity(it)
                    requireActivity().finishAffinity()
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {

            }
    }
}