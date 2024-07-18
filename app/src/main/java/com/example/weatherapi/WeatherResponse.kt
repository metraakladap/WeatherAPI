package com.example.weatherapi

data class WeatherResponse(
    val temperature: String,
    val wind: String,
    val description: String,
    val forecast: List<Forecast>
)

data class Forecast(
    val day: String,
    val temperature: String,
    val wind: String
)