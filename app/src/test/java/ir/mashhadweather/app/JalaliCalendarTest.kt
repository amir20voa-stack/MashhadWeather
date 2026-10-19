package ir.mashhadweather.app

import ir.mashhadweather.app.data.JalaliCalendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * تست واحد الگوریتم تقویم هجری شمسی.
 * اجرا: ./gradlew test
 */
class JalaliCalendarTest {

    @Test
    fun gregorianToJalali_knownDates() {
        assertEquals(
            JalaliCalendar.Jalali(1395, 1, 23),
            JalaliCalendar.gregorianToJalali(2016, 4, 11)
        )
        assertEquals(
            JalaliCalendar.Jalali(1396, 12, 29),
            JalaliCalendar.gregorianToJalali(2018, 3, 20)
        )
        assertEquals(
            JalaliCalendar.Jalali(1397, 1, 1),
            JalaliCalendar.gregorianToJalali(2018, 3, 21)
        )
        assertEquals(
            JalaliCalendar.Jalali(1403, 1, 1),
            JalaliCalendar.gregorianToJalali(2024, 3, 20)
        )
        assertEquals(
            JalaliCalendar.Jalali(1403, 12, 30),
            JalaliCalendar.gregorianToJalali(2025, 3, 20)
        )
        assertEquals(
            JalaliCalendar.Jalali(1404, 1, 1),
            JalaliCalendar.gregorianToJalali(2025, 3, 21)
        )
        assertEquals(
            JalaliCalendar.Jalali(1405, 1, 1),
            JalaliCalendar.gregorianToJalali(2026, 3, 21)
        )
    }

    @Test
    fun jalaliToGregorian_knownDates() {
        assertEquals(
            JalaliCalendar.Gregorian(2025, 3, 21),
            JalaliCalendar.jalaliToGregorian(1404, 1, 1)
        )
        assertEquals(
            JalaliCalendar.Gregorian(2026, 3, 21),
            JalaliCalendar.jalaliToGregorian(1405, 1, 1)
        )
    }

    @Test
    fun leapYears() {
        assertTrue(JalaliCalendar.isLeapJalaliYear(1399))
        assertTrue(JalaliCalendar.isLeapJalaliYear(1403))
        assertTrue(JalaliCalendar.isLeapJalaliYear(1408))
        assertFalse(JalaliCalendar.isLeapJalaliYear(1404))
        assertFalse(JalaliCalendar.isLeapJalaliYear(1405))
        assertEquals(30, JalaliCalendar.jalaliMonthLength(1403, 12))
        assertEquals(29, JalaliCalendar.jalaliMonthLength(1404, 12))
        assertEquals(31, JalaliCalendar.jalaliMonthLength(1405, 7))
        assertEquals(30, JalaliCalendar.jalaliMonthLength(1405, 10))
    }

    @Test
    fun roundTrip_1900_to_2100() {
        var date = LocalDate.of(1900, 1, 1)
        val end = LocalDate.of(2100, 12, 31)
        while (!date.isAfter(end)) {
            val jalali = JalaliCalendar.gregorianToJalali(date.year, date.monthValue, date.dayOfMonth)
            val gregorian = JalaliCalendar.jalaliToGregorian(jalali.year, jalali.month, jalali.day)
            assertEquals(
                "رفت‌وبرگشت ناموفق برای $date",
                date,
                LocalDate.of(gregorian.year, gregorian.month, gregorian.day)
            )
            date = date.plusDays(1)
        }
    }
}
