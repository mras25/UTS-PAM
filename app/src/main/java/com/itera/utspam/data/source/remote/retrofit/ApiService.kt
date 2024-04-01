package com.itera.utspam.data.source.remote.retrofit

import com.itera.utspam.data.model.SingleUserData
import com.itera.utspam.data.model.SingleUserResponse
import com.itera.utspam.data.model.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    //get list users with query
    @GET("api/users")
    fun getListUsers(@Query("page") page: String): Call<UserResponse>

    //get list user by id using path
    @GET("api/users/{id}")
    fun getUser(@Path("id") id: String): Call<SingleUserResponse>
}