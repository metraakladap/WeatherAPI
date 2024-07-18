package com.example.weatherapi

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherApi {
    @GET("weather/{city}")
    fun getWeather(@Path("city") city: String): Single<WeatherResponse>
}
