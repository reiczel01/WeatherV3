package com.example.weatherv3

data class WeatherDataForcast(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<Item>,
)


data class Item(
    val dt: Long,
    val main: MainForcast,
    val weather: List<WeatherForcast>,
    val dtTxt: String
)

data class MainForcast(
    val temp: Double,
    val pressure: Double,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double
)

data class WeatherForcast(
    val icon: String,
    val description: String
)
