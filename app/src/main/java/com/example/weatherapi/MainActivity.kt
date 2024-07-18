package com.example.weatherapi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var weatherCard: MaterialCardView
    private lateinit var cityNameTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var forecastLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherCard = findViewById(R.id.weather_card)
        cityNameTextView = findViewById(R.id.cityNameTextView)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        windTextView = findViewById(R.id.windTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        forecastLayout = findViewById(R.id.forecastLayout)
        progressBar = findViewById(R.id.progressBar)
        errorTextView = findViewById(R.id.errorTextView)

        fetchWeatherData("Kharkiv")
    }

    private fun fetchWeatherData(city: String) {
        showLoading()
        compositeDisposable.add(
            RetrofitClient.weatherApi.getWeather(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    hideLoading()
                    updateUI(city, response)
                }, { error ->
                    hideLoading()
                    showError("Error: ${error.message}")
                })
        )
    }

    private fun updateUI(city: String, weatherResponse: WeatherResponse) {
        weatherCard.visibility = View.VISIBLE
        cityNameTextView.text = city
        temperatureTextView.text = weatherResponse.temperature
        windTextView.text = "Wind: ${weatherResponse.wind}"
        descriptionTextView.text = weatherResponse.description

        forecastLayout.removeAllViews()
        weatherResponse.forecast.forEach { forecast ->
            val forecastView = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            forecastView.findViewById<TextView>(R.id.forecastDayTextView).text = "Day ${forecast.day}"
            forecastView.findViewById<TextView>(R.id.forecastTemperatureTextView).text = forecast.temperature
            forecastView.findViewById<TextView>(R.id.forecastWindTextView).text = forecast.wind
            forecastLayout.addView(forecastView)
        }
    }

    private fun showLoading() {
        weatherCard.visibility = View.GONE
        errorTextView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showError(message: String) {
        weatherCard.visibility = View.GONE
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = message
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}