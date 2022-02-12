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

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://mars.udacity.com/"
// define constants that match the query values our web service expects
enum class MarsApiFilter(val value: String) {SHOW_RENT("rent"), SHOW_BUY("buy"),
    SHOW_ALL("all")}

// moshi object using a moshi builder.
private val moshi = Moshi.Builder()
    // in order for Moshi's annotations to work properly with kotlin.
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * we're going to use this file to hold the network layer the api that the viewModel uses
 * to communicate with our web service.
 * */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    /** coroutine call adapter enable retrofit to produce a coroutine based API
     * call adapters add the ability for retrofit to create APIs that return other
     * than the default call class.
     * */
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface MarsApiService{
    @GET("realestate")
    fun getPropertiesAsync(@Query("filter") type: String):
        /**
         * deferred is a coroutine job that can directly return a result
         * a coroutine job provides a way of cancelling and determining the state of a coroutine
         * unlike a job deferred has a method called await which is a suspend function on the
         * deferred it causes the code to await without blocking in true coroutines fashion until the
         * value is ready and then returned.
         * */
        Deferred<List<MarsProperty>>
}

/** to create a retrofit service you call retrofit.create passing in the service interface API
 * we just defined.
 * */
object MarsApi{
    val retrofitService: MarsApiService by lazy {
        /** lazily initialized retrofit object
         * calling MarsApi.retrofitService will return a retrofit object that implements
         * MarsApiService.
         * */
        retrofit.create(MarsApiService::class.java)
    }
}
