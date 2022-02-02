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

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    /* The internal MutableLiveData String that stores the status of the most recent request */
    private val _response = MutableLiveData<String>()

    /**
     * The external immutable LiveData for the request status String
     * */
    val response: LiveData<String>
        get() = _response

    /**
     * since we are using coroutines we begin by creating a job thi allow us to use more straight
     * forward code and error handling
     */
    private var viewModelJob = Job()

    /**
     * since retrofit does all of its work on a background thread
     * there's no reason to use any other thread for our scope
     * */
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     * you must create your job and coroutine scope before the init block
     */
    init {
        getMarsRealEstateProperties()
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties() {
        coroutineScope.launch {
            /** coroutines are now managing concurrency
            calling getProperties from our MarsApiService creates and starts the network call in
            a background thread, returning the deferred.
            Calling await on the deferred returns he result from the network call when
            the value is ready.
            await is non blocking which means this will trigger our API service to retrieve the
            data from de network without blocking the current thread.
            once it's done the code continues executing from where it left off */
            val getPropertiesDeferred = MarsApi.retrofitService.getProperties()
            /**
             * we can now implement error handling as if the code wasn't happening asynchronously
             * */
                try{
                    var listResult = getPropertiesDeferred.await()
                    /** after awaiting on the deferred on the get properties line we can access the
                     * the return value and set that value into the response just as if the network
                     * operation wasn't happening in a background thread */
                    _response.value = "Success ${listResult.size} Mars properties retrieved"
                }catch (t: Throwable){
                    _response.value = "Failure " + t.message
                }
        }
    }

    /**
     * Loading data should stop when the viewModel is destroyed because the overview fragment
     * will be gone, so we override onCleared to cancel our job
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
