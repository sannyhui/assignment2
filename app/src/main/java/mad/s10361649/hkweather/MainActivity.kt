package mad.s10361649.hkweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import mad.s10361649.myapplication.R
import org.json.JSONArray
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    /** Drawables array */
    val weatherIcon = intArrayOf(
        R.drawable.pic50, R.drawable.pic51, R.drawable.pic52, R.drawable.pic53, R.drawable.pic54,
        R.drawable.pic60, R.drawable.pic61, R.drawable.pic62, R.drawable.pic63, R.drawable.pic64,
        R.drawable.pic65,
        R.drawable.pic70, R.drawable.pic71, R.drawable.pic72, R.drawable.pic73, R.drawable.pic74,
        R.drawable.pic75, R.drawable.pic76, R.drawable.pic77,
        R.drawable.pic80, R.drawable.pic81, R.drawable.pic82, R.drawable.pic83, R.drawable.pic84,
        R.drawable.pic85,
        R.drawable.pic91, R.drawable.pic92, R.drawable.pic93
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getTemp()  // Get today's temperature, weather icon, special warning and display.
        getForecast() // Get 9 days maximum temperature, weather icon, date and weekday and display
    }

    fun getTemp() {
        /**
         * To get today's temperature, weather icon, special warning
         * Variables list :-
         * TextView : showTemp, showWarn
         * ImageView : showWeather
         * JSON array : warningInfoArray
         * String : url, tempInfo, locationTemp, warningInfo, warningInfoChecking
         * Int : todayWeatherInt, todayWeatherIcon
         */
        var todayWeatherIcon =0 ; var warningInfo = "" // Initial variables
        val showTemp = findViewById<TextView>(R.id.temperatureTV) // Today temperature
        val showWarn = findViewById<TextView>(R.id.warningTV) // Special warning
        val showWeather = findViewById<ImageView>(R.id.showWeatherTV) // Weather image
        val url = "https://data.weather.gov.hk/weatherAPI/opendata/weather.php?dataType=rhrread&lang=en"
        val queue = Volley.newRequestQueue(this) // Volley command
        val jsonObjectRequest : JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener{response ->
                /** Get warning message array */
                val warningInfoChecking = response.getString("warningMessage")
                if (warningInfoChecking != "") { // Check whether array is there
                    val warningInfoArray = response.getJSONArray("warningMessage")
                    for (i in 0..warningInfoArray.length() - 1) {
                        warningInfo += warningInfoArray.optString(i) + "\n" // Convert JSON array to string
                    }
                }
                /** Get temperature string */
                val tempInfo = response.getJSONObject("temperature").getString("data")
                /** Get temperature from location "Hong Kong Observatory" */
                val locationTemp = JSONArray(tempInfo).getJSONObject(1).getString("value")
                /** Get weather icon array and convert record 0 to drawable */
                val todayWeatherInt = response.getJSONArray("icon").optInt(0)
                when (todayWeatherInt) {
                    in 50..54 -> todayWeatherIcon = weatherIcon[todayWeatherInt-50] // Array 0 ~ 4
                    in 60..65 -> todayWeatherIcon = weatherIcon[todayWeatherInt-55] // Array 5 ~ 10
                    in 70..77 -> todayWeatherIcon = weatherIcon[todayWeatherInt-59] // Array 11 ~ 18
                    in 80..85 -> todayWeatherIcon = weatherIcon[todayWeatherInt-61] // Array 19 ~ 24
                    in 91..93 -> todayWeatherIcon = weatherIcon[todayWeatherInt-66] // Array 25 ~ 27
                   else -> todayWeatherIcon = weatherIcon[0] // Set to sunny weather in case new icon number is released.
                }
                /** Display information to layout */
                showWarn.text = warningInfo // Display warning message
                showWarn.movementMethod = ScrollingMovementMethod() // Make textView scrollable
                showTemp.text = "$locationTemp\u2103" // Display temperature
                showWeather.setImageResource(todayWeatherIcon) // Display icon
            },
            Response.ErrorListener { error ->
                Log.e("ERROR", error.message.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }

    fun getForecast() {
        /**
         * To get 9 days forecast maximum temperature, weather icon, date and weekday and display
         * Variables list :-
         * RecyclerView : recyclerView
         * Adapter : customAdapter
         * Layout Manager : layoutManager
         * Array <String> : wForecast
         * Array <Int> : wForecastIconNo, wForecastIcon
         * String : url, weatherForecast
         */
        val url = "https://data.weather.gov.hk/weatherAPI/opendata/weather.php?dataType=fnd"
        val queue = Volley.newRequestQueue(this) // Volley command
        val wForecast = Array<String>(9,{" "}) // Size = 9 and assign " " by default
        val wForecastIconNo = Array<Int>(9,{ it -> it * 0}) // Size = 9 and assign 0 by default
        val wForecastIcon = Array<Int>(9,{ it -> it * 0}) // Size = 9 and assign 0 by default
        val jsonObjectRequest : JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener{response ->
                val weatherForecast = response.getString("weatherForecast") // Download API data as string
                for (i in 0..8) { // Get fields (Date + Weekday + Max. temperature) from weatherForecast string
                    wForecast[i] = JSONArray(weatherForecast).getJSONObject(i).getString("forecastDate") + " " +
                        JSONArray(weatherForecast).getJSONObject(i).getString("week") + " " +
                        JSONArray(weatherForecast).getJSONObject(i).getJSONObject("forecastMaxtemp").getString("value") +
                             "℃"
                    /** Get icon number from weatherForecast string and convert to drawable. */
                    wForecastIconNo[i] = JSONArray(weatherForecast).getJSONObject(i).getInt("ForecastIcon")
                    /** Get icon from drawable array */
                    when (wForecastIconNo[i]) {
                        in 50..54 -> wForecastIcon[i] = weatherIcon[wForecastIconNo[i]-50] // 0 ~ 4
                        in 60..65 -> wForecastIcon[i] = weatherIcon[wForecastIconNo[i]-55] // 5 ~ 10
                        in 70..77 -> wForecastIcon[i] = weatherIcon[wForecastIconNo[i]-59] // 11 ~ 18
                        in 80..85 -> wForecastIcon[i] = weatherIcon[wForecastIconNo[i]-61] // 19 ~ 24
                        in 91..93 -> wForecastIcon[i] = weatherIcon[wForecastIconNo[i]-66] // 25 ~ 27
                        else -> wForecastIcon[i] = weatherIcon[0]
                    }
                }
                /** Put icon and text array to recycler view */
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                val customAdapter = CustomAdapter(wForecast.toList(),wForecastIcon)
                val layoutManager = LinearLayoutManager(applicationContext)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = customAdapter
            },
            Response.ErrorListener { error ->
                Log.e("ERROR", error.message.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }
}