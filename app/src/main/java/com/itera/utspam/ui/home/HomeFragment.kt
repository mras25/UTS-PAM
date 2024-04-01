package com.itera.utspam.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.itera.utspam.data.model.DataItem
import com.itera.utspam.data.model.UserResponse
import com.itera.utspam.data.source.remote.retrofit.ApiConfig
import com.itera.utspam.databinding.FragmentHomeBinding
import com.itera.utspam.ui.detailuser.DetailUserActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: HomeAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HomeAdapter(mutableListOf(), onItemClick = object: HomeAdapter.OnItemClick {
            override fun onItemClicked(id: Int) {
                Intent(requireActivity(), DetailUserActivity::class.java).also {
                    it.putExtra(DetailUserActivity.EXTRA_ID, id)
                    startActivity(it)
                }
            }
        })

        getUser()

        with(binding) {
            rvUser.layoutManager = LinearLayoutManager(requireContext())
            rvUser.adapter = adapter

            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->

                searchView.hide()

                getUser(searchView.text.toString())

                false
            }
        }

    }

    private fun getUser(nameQuery: String = "") {
        val client = ApiConfig.getApiService().getListUsers("1")

        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                adapter.clear()

                if (response.isSuccessful) {
                    val dataArray = response.body()?.data as List<DataItem>
                    for (data in dataArray) {
                        if (nameQuery == "") {
                            adapter.addUser(data)
                        } else {
                            if (data.firstName?.lowercase()?.contains(nameQuery.lowercase()) == true || data.lastName?.lowercase()?.contains(nameQuery.lowercase()) == true) {
                                adapter.addUser(data)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}