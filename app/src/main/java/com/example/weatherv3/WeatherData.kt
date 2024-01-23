package com.example.weatherv3

data class WeatherData(
    val name: String,
    val main: Main,
    val wind: Wind,
    val weather: List<Weather>
)

data class Main(
    val temp: Double,
    val pressure: Double,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double
)

data class Weather(
    val icon: String,
    val description: String
)

data class Wind(
    val speed: Double,
    val deg: Int
)