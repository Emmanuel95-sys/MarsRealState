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

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

// If our data class has property names that match the properties in out Json response
// Moshi machets them by name  and fills the data objects wit appropiate values and types
@Parcelize
data class MarsProperty(
    val id: String,
    // to use property names that differ form the attributes in the JSON response
    // we use the @Json(name="property_name_as_in_json" )
    @Json(name = "img_src")
    val imgSrcUrl: String,
    val type: String,
    val price: Double
) : Parcelable{
    val isRental
    get() = type =="rent"
}
