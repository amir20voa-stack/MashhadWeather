package ir.mashhadweather.app.data

/**
 * کدهای وضعیت هوا مطابق استاندارد WMO که Open-Meteo برمی‌گرداند،
 * به همراه برچسب فارسی و نوع آیکون.
 */
enum class WeatherKind {
    CLEAR, PARTLY, CLOUDY, FOG, DRIZZLE, RAIN, SNOW, SHOWERS, THUNDER
}

object WeatherCodes {

    /** توضیح فارسی وضعیت هوا بر اساس کد WMO */
    fun label(code: Int): String = when (code) {
        0 -> "آسمان صاف"
        1 -> "عمدتاً صاف"
        2 -> "نیمه‌ابری"
        3 -> "ابری"
        45, 48 -> "مه‌آلود"
        51 -> "نم‌نم باران خفیف"
        53 -> "نم‌نم باران"
        55 -> "نم‌نم باران شدید"
        56, 57 -> "نم‌نم باران یخ‌زده"
        61 -> "باران خفیف"
        63 -> "باران"
        65 -> "باران شدید"
        66, 67 -> "باران یخ‌زده"
        71 -> "بارش برف خفیف"
        73 -> "بارش برف"
        75 -> "بارش برف سنگین"
        77 -> "دانه‌های برف"
        80 -> "رگبار باران خفیف"
        81 -> "رگبار باران"
        82 -> "رگبار باران شدید"
        85, 86 -> "رگبار برف"
        95 -> "رعد و برق"
        96, 99 -> "رعد و برق با تگرگ"
        else -> "نامشخص"
    }

    /** نوع آیکون بر اساس کد WMO */
    fun kind(code: Int): WeatherKind = when (code) {
        0 -> WeatherKind.CLEAR
        1, 2 -> WeatherKind.PARTLY
        3 -> WeatherKind.CLOUDY
        45, 48 -> WeatherKind.FOG
        51, 53, 55, 56, 57 -> WeatherKind.DRIZZLE
        61, 63, 65, 66, 67 -> WeatherKind.RAIN
        71, 73, 75, 77 -> WeatherKind.SNOW
        80, 81, 82, 85, 86 -> WeatherKind.SHOWERS
        95, 96, 99 -> WeatherKind.THUNDER
        else -> WeatherKind.CLOUDY
    }
}
