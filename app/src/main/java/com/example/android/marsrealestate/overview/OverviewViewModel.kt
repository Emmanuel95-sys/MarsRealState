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
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class MarsApiStatus{LOADING, ERROR, DONE}
/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    /**
     * this live data is going to primarily be storing errors
     * */
    private val _status = MutableLiveData<MarsApiStatus>()
    /**
     * The external immutable LiveData for the request status String
     * */
    val status: LiveData<MarsApiStatus>
        get() = _status

    /**
     * LiveData for Mars properties
     * internal MutableLiveData
     * external LiveData
     */
    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties: LiveData<List<MarsProperty>>
    get() = _properties

    /**
     * Navigation in this architecture works by triggering a LiveData change in the viewModel
     * for that we create a Mutable Live Data navigation property that we expose a LiveData
     * */
    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty : LiveData<MarsProperty>
    get() = _navigateToSelectedProperty

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
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        coroutineScope.launch {
            /** coroutines are now managing concurrency
            calling getProperties from our MarsApiService creates and starts the network call in
            a background thread, returning the deferred.
            Calling await on the deferred returns he result from the network call when
            the value is ready.
            await is non blocking which means this will trigger our API service to retrieve the
            data from de network without blocking the current thread.
            once it's done the code continues executing from where it left off */
            val getPropertiesDeferred = MarsApi.retrofitService.getPropertiesAsync(filter.value)
            /**
             * we can now implement error handling as if the code wasn't happening asynchronously
             * */
                try{
                    /**
                     * we have 3 states to consider loading, success and failure
                     * the loading state happens while we're waiting for data with the await call*/
                    _status.value = MarsApiStatus.LOADING
                    val listResult = getPropertiesDeferred.await()
                    /** after awaiting on the deferred on the get properties line we can access the
                     * the return value and set that value into the response just as if the network
                     * operation wasn't happening in a background thread */
                    _status.value = MarsApiStatus.DONE
                    if(listResult.isNotEmpty()){
                        _properties.value = listResult
                    }
                }catch (t: Throwable){
                    _status.value = MarsApiStatus.ERROR
                    _properties.value = ArrayList()
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

    /**
     * following the navigation pattern by adding a method that sets
     * our navigateToSelectedProperty MLD to the selected MarsProperty
     * */

    fun displayPropertyDetails(marsProperty: MarsProperty){
        _navigateToSelectedProperty.value = marsProperty
    }

    /**
     * clear the LD to avoid being triggered again when we return from the detailView
     * we add a second method
     * */

    fun displayPropertyDetailsComplete(){
        _navigateToSelectedProperty.value = null
    }

    /**
     * Method that takes a filter input and reload the properties with getMarsRealEstateProperties
     * using the filter value
     * */
    fun updateFilter(filter: MarsApiFilter){
        getMarsRealEstateProperties(filter)
    }
}
