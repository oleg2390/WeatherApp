package com.example.weatherapp.core.network.dto

import com.google.gson.annotations.SerializedName

data class GeoCodingDto(
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String? = null,
    @SerializedName("local_names") val localNames: Map<String, String>? = null
)
