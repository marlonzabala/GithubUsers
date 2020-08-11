package com.app.githubusers.network

import com.app.githubusers.network.model.User
import com.app.githubusers.network.model.UserList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Handling api calls using retrofit library
 */
const val BASE_URL = "https://api.github.com/"

interface UserNetwork {
    @GET("users?")
    fun getUserListSince(
        @Query("since") since : Int,
        @Query("per_page") per_page: Int
    ) : Call<UserList>

    @GET("users/{username}")
    fun getUser(
        @Path(value = "username", encoded = true) username : String
    ) : Call<User>
}