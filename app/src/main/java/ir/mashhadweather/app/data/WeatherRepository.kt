package ir.mashhadweather.app.data

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * دریافت داده آب‌وهوای مشهد از سرویس رایگان Open-Meteo
 * (بدون نیاز به کلید API).
 *
 * مختصات مشهد: ۳۶٫۲۹۷۲ شمالی، ۵۹٫۶۰۶۷ شرقی
 */
class WeatherRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    /** درخواست همزمان (باید در Dispatchers.IO فراخوانی شود) */
    fun fetchForecast(): ForecastResponse {
        val request = Request.Builder()
            .url(buildUrl())
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("پاسخ ناموفق سرور: ${response.code}")
            }
            val body = response.body?.string()
                ?: throw IOException("پاسخ خالی از سرور")
            return json.decodeFromString(ForecastResponse.serializer(), body)
        }
    }

    private fun buildUrl(): String =
        "https://api.open-meteo.com/v1/forecast" +
                "?latitude=36.2972&longitude=59.6067" +
                "&current=temperature_2m,relative_humidity_2m,apparent_temperature," +
                "is_day,precipitation,weather_code,cloud_cover,wind_speed_10m," +
                "wind_direction_10m,surface_pressure" +
                "&hourly=temperature_2m,weather_code,precipitation_probability" +
                "&daily=weather_code,temperature_2m_max,temperature_2m_min," +
                "sunrise,sunset,precipitation_probability_max,wind_speed_10m_max" +
                "&timezone=Asia%2FTehran" +
                "&forecast_days=7"
}
