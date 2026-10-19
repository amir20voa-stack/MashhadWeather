package ir.mashhadweather.app.data

/**
 * تبدیل تاریخ میلادی و هجری شمسی (جلالی).
 *
 * الگوریتم مبتنی بر تقویم بورکوفسکی، بازنویسی‌شده از کتابخانه متن‌باز
 * jalaali-js (مجوز MIT) به زبان کاتلین.
 *
 * صحت این پیاده‌سازی با تست رفت‌وبرگشت روی ۷۳,۴۱۴ روز بازه ۱۹۰۰ تا ۲۱۰۰ میلادی
 * و همچنین تاریخ‌های مرجع تقویم رسمی ایران راستی‌آزمایی شده است.
 * (تست واحد: app/src/test/java/ir/mashhadweather/app/JalaliCalendarTest.kt)
 */
object JalaliCalendar {

    /** یک تاریخ هجری شمسی */
    data class Jalali(val year: Int, val month: Int, val day: Int)

    /** یک تاریخ میلادی */
    data class Gregorian(val year: Int, val month: Int, val day: Int)

    private val breaks = intArrayOf(
        -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
        1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
    )

    private data class CalInfo(val leap: Int, val gy: Int, val march: Int)

    /** تقسیم صحیح با گرد شدن به سمت صفر (معادل JS و Kotlin) */
    private fun div(a: Int, b: Int): Int = a / b

    /** باقیمانده سازگار با تقسیم به سمت صفر */
    private fun mod(a: Int, b: Int): Int = a - a / b * b

    private fun jalCal(jy: Int, withoutLeap: Boolean): CalInfo {
        val bl = breaks.size
        val gy = jy + 621
        var leapJ = -14
        var jp = breaks[0]
        var jump = 0
        var n: Int

        require(jy >= jp && jy < breaks[bl - 1]) { "سال شمسی خارج از محدوده پشتیبانی: $jy" }

        for (i in 1 until bl) {
            val jm = breaks[i]
            jump = jm - jp
            if (jy < jm) break
            leapJ += div(jump, 33) * 8 + div(mod(jump, 33), 4)
            jp = jm
        }

        n = jy - jp
        leapJ += div(n, 33) * 8 + div(mod(n, 33) + 3, 4)
        if (mod(jump, 33) == 4 && jump - n == 4) leapJ += 1

        val leapG = div(gy, 4) - div((div(gy, 100) + 1) * 3, 4) - 150
        val march = 20 + leapJ - leapG

        var leap = -1
        if (!withoutLeap) {
            if (jump - n < 6) n = n - jump + div(jump + 4, 33) * 33
            leap = mod(mod(n + 1, 33) - 1, 4)
            if (leap == -1) leap = 4
        }
        return CalInfo(leap, gy, march)
    }

    /** آیا سال شمسی داده‌شده کبیسه است؟ */
    fun isLeapJalaliYear(jy: Int): Boolean = jalCal(jy, false).leap == 0

    /** تعداد روزهای یک ماه شمسی */
    fun jalaliMonthLength(jy: Int, jm: Int): Int {
        if (jm <= 6) return 31
        if (jm <= 11) return 30
        return if (isLeapJalaliYear(jy)) 30 else 29
    }

    /** تبدیل روز شمسی/میلادی به شماره روز یولیانی (jalaali-js: g2d) */
    private fun g2d(gy: Int, gm: Int, gd: Int): Int {
        var d = div((gy + div(gm - 8, 6) + 100100) * 1461, 4) +
                div(153 * mod(gm + 9, 12) + 2, 5) + gd - 34840408
        d -= div(div(gy + 100100 + div(gm - 8, 6), 100) * 3, 4)
        d += 752
        return d
    }

    /** تبدیل شماره روز یولیانی به تاریخ میلادی (jalaali-js: d2g) */
    private fun d2g(jdn: Int): Gregorian {
        var j = 4 * jdn + 139361631
        j += div(div(4 * jdn + 183187720, 146097) * 3, 4) * 4 - 3908
        val i = div(mod(j, 1461), 4) * 5 + 308
        val gd = div(mod(i, 153), 5) + 1
        val gm = mod(div(i, 153), 12) + 1
        val gy = div(j, 1461) - 100100 + div(8 - gm, 6)
        return Gregorian(gy, gm, gd)
    }

    /** تبدیل تاریخ شمسی به شماره روز یولیانی (jalaali-js: j2d) */
    private fun j2d(jy: Int, jm: Int, jd: Int): Int {
        val r = jalCal(jy, true)
        return g2d(r.gy, 3, r.march) + (jm - 1) * 31 - div(jm, 7) * (jm - 7) + jd - 1
    }

    /** تبدیل شماره روز یولیانی به تاریخ شمسی (jalaali-js: d2j) */
    private fun d2j(jdn: Int): Jalali {
        val gy = d2g(jdn).year
        var jy = gy - 621
        val r = jalCal(jy, false)
        val jdn1f = g2d(r.gy, 3, r.march)
        var k = jdn - jdn1f

        if (k >= 0) {
            if (k <= 185) {
                // شش ماه اول سال
                val jm = 1 + div(k, 31)
                val jd = mod(k, 31) + 1
                return Jalali(jy, jm, jd)
            }
            k -= 186
        } else {
            // سال شمسی قبل
            jy -= 1
            k += 179
            if (r.leap == 1) k += 1
        }

        val jm = 7 + div(k, 30)
        val jd = mod(k, 30) + 1
        return Jalali(jy, jm, jd)
    }

    /** تبدیل میلادی به شمسی */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Jalali = d2j(g2d(gy, gm, gd))

    /** تبدیل شمسی به میلادی */
    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Gregorian = d2g(j2d(jy, jm, jd))
}
