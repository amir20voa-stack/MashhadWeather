package ir.mashhadweather.app.util

/** ارقام فارسی */
private val faDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

/** تبدیل ارقام لاتین رشته به ارقام فارسی */
fun String.toPersianDigits(): String = buildString {
    for (c in this@toPersianDigits) {
        append(if (c in '0'..'9') faDigits[c - '0'] else c)
    }
}

/** تبدیل عدد صحیح به رشته با ارقام فارسی */
fun Int.toPersian(): String = toString().toPersianDigits()

/** نمایش دما با رقم فارسی، مثلاً «۲۴°» */
fun Double.toTemp(): String = "${toInt().toString().toPersianDigits()}°"

/** نمایش دما از یک عدد صحیح */
fun Int.toTemp(): String = "${toString().toPersianDigits()}°"

/** نام ماه‌های شمسی */
fun jalaliMonthName(month: Int): String = listOf(
    "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
)[month - 1]

/** نام ماه‌های میلادی به فارسی */
fun gregorianMonthName(month: Int): String = listOf(
    "ژانویه", "فوریه", "مارس", "آوریل", "مه", "ژوئن",
    "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر"
)[month - 1]

/** نام روزهای هفته به فارسی */
fun java.time.DayOfWeek.persianName(): String = when (this) {
    java.time.DayOfWeek.SATURDAY -> "شنبه"
    java.time.DayOfWeek.SUNDAY -> "یکشنبه"
    java.time.DayOfWeek.MONDAY -> "دوشنبه"
    java.time.DayOfWeek.TUESDAY -> "سه‌شنبه"
    java.time.DayOfWeek.WEDNESDAY -> "چهارشنبه"
    java.time.DayOfWeek.THURSDAY -> "پنجشنبه"
    java.time.DayOfWeek.FRIDAY -> "جمعه"
}

/** نام جهت باد بر اساس درجه (جهتی که باد از آن می‌وزد) */
fun windDirectionName(deg: Int): String = when (((deg % 360 + 360 + 22) % 360) / 45) {
    0 -> "شمال"
    1 -> "شمال‌شرق"
    2 -> "شرق"
    3 -> "جنوب‌شرق"
    4 -> "جنوب"
    5 -> "جنوب‌غرب"
    6 -> "غرب"
    else -> "شمال‌غرب"
}
