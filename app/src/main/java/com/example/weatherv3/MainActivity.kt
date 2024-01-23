package com.example.weatherv3


import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.weatherv3.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val apiKey = "dd3341436b8a3e25fd2ef37366b913c4"

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkInternetConnection(this)
        replaceFragment(Fragment1())


        binding.currentWeather.setOnClickListener {

            replaceFragment(Fragment1())

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val city = "lodz" // Tutaj wpisz nazwę pożądanego miasta
                    val weatherData = getWeatherData(city, apiKey)
                    updateUI(weatherData)
                } catch (e: Exception) {
                    // Tutaj obsługuj ogólny błąd, np. wyświetl komunikat użytkownikowi
                    e.printStackTrace()
                }
            }

        }

        binding.forecast.setOnClickListener {

            replaceFragment(Fragment2())

        }

        binding.localization.setOnClickListener {

            replaceFragment(Fragment3())

        }


        GlobalScope.launch(Dispatchers.Main) {
            try {
                val city = "lodz" // Tutaj wpisz nazwę pożądanego miasta
                val weatherData = getWeatherForcastData(city, apiKey)
                //updateUIForcast(weatherData)
            } catch (e: Exception) {
                // Tutaj obsługuj ogólny błąd, np. wyświetl komunikat użytkownikowi
                e.printStackTrace()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
    }

    private fun updateUI(weatherData: WeatherData) {
        findViewById<TextView>(R.id.textViewCity).text = weatherData.name
        findViewById<TextView>(R.id.textViewTemperature).text =
            "${weatherData.main.temp.toDouble()}°C"
        findViewById<TextView>(R.id.wind_speed).text =
            "Prędkość wiatru:\n${weatherData.wind.speed} m/s"
        findViewById<TextView>(R.id.wind_degre).text =
            "Kierunek wiatru:\n${degreesToDirection(weatherData.wind.deg)}"
        findViewById<TextView>(R.id.pressure).text =
            "Ciśnienie:\n${weatherData.main.pressure} hPa"
        findViewById<TextView>(R.id.humidity).text =
            "Wilgotność:\n${weatherData.main.humidity}%"
        findViewById<TextView>(R.id.temp_min).text =
            "Temperatura minimalna:\n${weatherData.main.temp_min}°C"
        findViewById<TextView>(R.id.temp_max).text =
            "Temperatura maksymalna:\n${weatherData.main.temp_max}°C"
        findViewById<TextView>(R.id.description).text =
            "${translateWeatherDescription(weatherData.weather[0].description)}"
        val iconUrl =
            "https://openweathermap.org/img/wn/${weatherData.weather[0].icon}@4x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(findViewById(R.id.imageViewWeatherIcon))
    }


}
