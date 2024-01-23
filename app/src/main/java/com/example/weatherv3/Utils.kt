package com.example.weatherv3


import android.content.Context
import android.media.MediaParser
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.JsonParseException

import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun degreesToDirection(degrees: Int): String {
    val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")

    val index = ((degrees % 360 + 360) % 360) / 45
    return directions[index.toInt()]
}

fun checkInternetConnection(context: Context) {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkInfo = connectivityManager.activeNetworkInfo

    if (networkInfo != null && networkInfo.isConnected) {
        // Sprawdź dostęp do Internetu
        if (isInternetAvailable(connectivityManager)) {
            // Internet jest dostępny
            Toast.makeText(context, "Uzyskano dostęp do Internetu", Toast.LENGTH_SHORT).show()
        } else {
            // Brak dostępu do Internetu
            Toast.makeText(context, "Brak dostępu do Internetu", Toast.LENGTH_SHORT).show()
        }
    } else {
        // Brak połączenia z siecią
        Toast.makeText(context, "Brak połączenia z siecią", Toast.LENGTH_SHORT).show()
    }
}

@Suppress("DEPRECATION")
fun isInternetAvailable(connectivityManager: ConnectivityManager): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}

suspend fun getWeatherData(city: String, apiKey: String): WeatherData {
    val weatherService = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherService::class.java)

    return withContext(Dispatchers.IO) {
        try {
            val weatherData = weatherService.getWeather(city, "metric", apiKey)
            weatherData // zwraca dane pogodowe
        } catch (e: Exception) {
            // Tutaj możesz obsłużyć błędy zapytania lub parsowania
            e.printStackTrace()
            throw e
        }
    }
}

suspend fun getWeatherForcastData(city: String, apiKey: String): WeatherDataForcast {
    val weatherForcastService = Retrofit.Builder()
        .baseUrl("http://api.openweathermap.org/data/2.5/forecast/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherForcastService::class.java)

    return withContext(Dispatchers.IO) {
        try {
            val weatherDataForcast = weatherForcastService.getWeatherForcast(city, "metric", apiKey)
            weatherDataForcast // zwraca dane pogodowe
        } catch (e: Exception) {
            // Tutaj możesz obsłużyć błędy zapytania lub parsowania
            e.printStackTrace()
            throw e
        }

    }
}



fun translateWeatherDescription(weatherDescription: String): String {
    val translationMap = mapOf(
        // Group 2xx: Thunderstorm
        "thunderstorm with light rain" to "burza z lekkim deszczem",
        "thunderstorm with rain" to "burza z deszczem",
        "thunderstorm with heavy rain" to "burza z intensywnym deszczem",
        "light thunderstorm" to "lekka burza",
        "thunderstorm" to "burza",
        "heavy thunderstorm" to "silna burza",
        "ragged thunderstorm" to "poszarpana burza",
        "thunderstorm with light drizzle" to "burza z lekką mżawką",
        "thunderstorm with drizzle" to "burza z mżawką",
        "thunderstorm with heavy drizzle" to "burza z intensywną mżawką",

        // Group 3xx: Drizzle
        "light intensity drizzle" to "lekka mżawka",
        "drizzle" to "mżawka",
        "heavy intensity drizzle" to "intensywna mżawka",
        "light intensity drizzle rain" to "lekka mżawka z deszczem",
        "drizzle rain" to "deszcz z mżawką",
        "heavy intensity drizzle rain" to "intensywny deszcz z mżawką",
        "shower rain and drizzle" to "deszcz i mżawka",
        "heavy shower rain and drizzle" to "silny deszcz i mżawka",
        "shower drizzle" to "mżawka podczas przelotnych opadów",

        // Group 5xx: Rain
        "light rain" to "lekki deszcz",
        "moderate rain" to "umiarkowany deszcz",
        "heavy intensity rain" to "intensywny deszcz",
        "very heavy rain" to "bardzo intensywny deszcz",
        "extreme rain" to "ekstremalny deszcz",
        "freezing rain" to "marznący deszcz",
        "light intensity shower rain" to "lekki przelotny deszcz",
        "shower rain" to "przelotny deszcz",
        "heavy intensity shower rain" to "intensywny przelotny deszcz",
        "ragged shower rain" to "przelotny deszcz o nieregularnej intensywności",

        // Group 6xx: Snow
        "light snow" to "lekki śnieg",
        "snow" to "śnieg",
        "heavy snow" to "silny śnieg",
        "sleet" to "śnieg z deszczem",
        "light shower sleet" to "lekki śnieg z deszczem",
        "shower sleet" to "śnieg z deszczem",
        "light rain and snow" to "lekki deszcz ze śniegiem",
        "rain and snow" to "deszcz ze śniegiem",
        "light shower snow" to "lekki przelotny śnieg",
        "shower snow" to "przelotny śnieg",
        "heavy shower snow" to "intensywny przelotny śnieg",

        // Group 7xx: Atmosphere
        "mist" to "mgła",
        "smoke" to "dym",
        "haze" to "mgła spowodowana pyłem",
        "sand/dust whirls" to "wirujący piasek/kurz",
        "fog" to "mgła",
        "sand" to "piasek",
        "dust" to "kurz",
        "volcanic ash" to "pył wulkaniczny",
        "squalls" to "szkwały",
        "tornado" to "tornado",

        // Group 800: Clear
        "clear sky" to "czyste niebo",

        // Group 80x: Clouds
        "few clouds" to "małe zachmurzenie: 11-25%",
        "scattered clouds" to "rozproszone chmury: 25-50%",
        "broken clouds" to "rozległe zachmurzenie: 51-84%",
        "overcast clouds" to "pełne zachmurzenie: 85-100%"
    )

    return translationMap.getOrElse(weatherDescription) { weatherDescription }
}