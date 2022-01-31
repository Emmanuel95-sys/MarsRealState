/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://mars.udacity.com/"

// moshi object using a moshi builder.
private val moshi = Moshi.Builder()
    // in order for Moshi's annotations to work properly with kotlin
    .add(KotlinJsonAdapterFactory())
    .build()

// we're going to use this file to hold the network layer
// the api that the viewModel uses to communicate with our web service
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MarsApiService{
    @GET("realestate")
    fun getProperties():
        //call object is used to start the request
        Call<List<MarsProperty>>
}

// to create a retrofit service you call retrofit.create passing in the service interface API
// we just defined
object MarsApi{
    val retrofitService: MarsApiService by lazy {
        // lazily initialized retrofit object
        // calling MarsApi.retrofitService will return a retrofit object that implements
        // MarsApiService
        retrofit.create(MarsApiService::class.java)
    }
}
