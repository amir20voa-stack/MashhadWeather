package ir.mashhadweather.app.data

import ir.mashhadweather.app.util.jalaliMonthName
import ir.mashhadweather.app.util.persianName
import ir.mashhadweather.app.util.toPersian
import ir.mashhadweather.app.util.toTemp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

// ==================== مدل‌های خام پاسخ Open-Meteo ====================

@Serializable
data class ForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: Current,
    val hourly: Hourly,
    val daily: Daily
)

@Serializable
data class Current(
    val time: String,
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("relative_humidity_2m") val humidity: Int,
    @SerialName("apparent_temperature") val apparentTemperature: Double,
    @SerialName("is_day") val isDay: Int,
    val precipitation: Double,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("cloud_cover") val cloudCover: Int,
    @SerialName("wind_speed_10m") val windSpeed: Double,
    @SerialName("wind_direction_10m") val windDirection: Int,
    @SerialName("surface_pressure") val surfacePressure: Double
)

@Serializable
data class Hourly(
    val time: List<String>,
    @SerialName("temperature_2m") val temperature: List<Double>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("precipitation_probability") val precipitationProbability: List<Int>
)

@Serializable
data class Daily(
    val time: List<String>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min") val temperatureMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>,
    @SerialName("wind_speed_10m_max") val windSpeedMax: List<Double>
)

// ==================== مدل‌های آماده نمایش (UI) ====================

/** یک ساعت از پیش‌بینی ساعتی */
data class HourUi(
    val time: LocalDateTime,
    val temperature: Int,
    val weatherCode: Int,
    val precipitationProbability: Int
)

/** یک روز از پیش‌بینی روزانه */
data class DailyUi(
    val date: LocalDate,
    val jalali: JalaliCalendar.Jalali,
    val weatherCode: Int,
    val min: Int,
    val max: Int,
    val precipitationProbability: Int
)

/** مدل کامل صفحه آب‌وهوا */
data class WeatherUi(
    val currentTemp: Int,
    val feelsLike: Int,
    val description: String,
    val weatherCode: Int,
    val isDay: Boolean,
    val humidity: Int,
    val windSpeed: Int,
    val windDirection: Int,
    val pressure: Int,
    val cloudCover: Int,
    val todayMax: Int,
    val todayMin: Int,
    val todayJalali: JalaliCalendar.Jalali,
    val todayWeekday: String,
    val sunrise: String,
    val sunset: String,
    val hourly: List<HourUi>,
    val daily: List<DailyUi>
) {
    /** تاریخ امروز به شکل «دوشنبه، ۲۷ مهر ۱۴۰۵» */
    val todayFullDate: String
        get() = "${todayWeekday}، ${todayJalali.day.toPersian()} " +
                "${jalaliMonthName(todayJalali.month)} ${todayJalali.year.toPersian()}"
}

/**
 * تبدیل پاسخ خام Open-Meteo به مدل آماده نمایش.
 * همه محاسبات زمانی بر مبنای منطقه زمانی Asia/Tehran انجام می‌شود.
 */
fun ForecastResponse.toWeatherUi(): WeatherUi {
    val tehranNow = LocalDateTime.now(ZoneId.of("Asia/Tehran"))
    val todayDate = tehranNow.toLocalDate()

    // ایندکس امروز در آرایه‌های روزانه
    val todayIdx = daily.time.indexOfFirst { LocalDate.parse(it) == todayDate }
        .coerceAtLeast(0)

    val jalaliToday = JalaliCalendar.gregorianToJalali(
        todayDate.year, todayDate.monthValue, todayDate.dayOfMonth
    )

    // برش ۲۴ ساعت آینده از پیش‌بینی ساعتی
    val currentHour = tehranNow.truncatedTo(ChronoUnit.HOURS)
    val startIdx = hourly.time
        .indexOfFirst { LocalDateTime.parse(it) >= currentHour }
        .let { if (it < 0) 0 else it }
    val endIdx = minOf(startIdx + 24, hourly.time.size)
    val hours = (startIdx until endIdx).map { i ->
        HourUi(
            time = LocalDateTime.parse(hourly.time[i]),
            temperature = hourly.temperature[i].toInt(),
            weatherCode = hourly.weatherCode[i],
            precipitationProbability = hourly.precipitationProbability[i]
        )
    }

    // پیش‌بینی روزانه
    val days = daily.time.indices.map { i ->
        val date = LocalDate.parse(daily.time[i])
        DailyUi(
            date = date,
            jalali = JalaliCalendar.gregorianToJalali(date.year, date.monthValue, date.dayOfMonth),
            weatherCode = daily.weatherCode[i],
            min = daily.temperatureMin[i].toInt(),
            max = daily.temperatureMax[i].toInt(),
            precipitationProbability = daily.precipitationProbabilityMax[i]
        )
    }

    return WeatherUi(
        currentTemp = current.temperature.toInt(),
        feelsLike = current.apparentTemperature.toInt(),
        description = WeatherCodes.label(current.weatherCode),
        weatherCode = current.weatherCode,
        isDay = current.isDay == 1,
        humidity = current.humidity,
        windSpeed = current.windSpeed.toInt(),
        windDirection = current.windDirection,
        pressure = current.surfacePressure.toInt(),
        cloudCover = current.cloudCover,
        todayMax = daily.temperatureMax[todayIdx].toInt(),
        todayMin = daily.temperatureMin[todayIdx].toInt(),
        todayJalali = jalaliToday,
        todayWeekday = todayDate.dayOfWeek.persianName(),
        sunrise = daily.sunrise[todayIdx].takeIf { it.length >= 16 }?.substring(11, 16) ?: "--:--",
        sunset = daily.sunset[todayIdx].takeIf { it.length >= 16 }?.substring(11, 16) ?: "--:--",
        hourly = hours,
        daily = days
    )
}
