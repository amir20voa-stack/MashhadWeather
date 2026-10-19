package ir.mashhadweather.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.mashhadweather.app.R
import ir.mashhadweather.app.data.JalaliCalendar
import ir.mashhadweather.app.data.Occasions
import ir.mashhadweather.app.util.gregorianMonthName
import ir.mashhadweather.app.util.jalaliMonthName
import ir.mashhadweather.app.util.persianName
import ir.mashhadweather.app.util.toPersian
import java.time.LocalDate
import java.time.ZoneId

/**
 * صفحه تقویم هجری شمسی:
 * نمایش ماه، جابه‌جایی بین ماه‌ها و مناسبت‌های روز انتخابی.
 */
@Composable
fun CalendarScreen() {
    val todayGregorian = remember { LocalDate.now(ZoneId.of("Asia/Tehran")) }
    val today = remember {
        JalaliCalendar.gregorianToJalali(
            todayGregorian.year,
            todayGregorian.monthValue,
            todayGregorian.dayOfMonth
        )
    }

    var viewYear by remember { mutableIntStateOf(today.year) }
    var viewMonth by remember { mutableIntStateOf(today.month) }
    var selected by remember { mutableStateOf(today) }

    val monthLength = remember(viewYear, viewMonth) {
        JalaliCalendar.jalaliMonthLength(viewYear, viewMonth)
    }

    // ستون شروع ماه: شنبه ستون اول تقویم ایرانی است
    val firstWeekdayOffset = remember(viewYear, viewMonth) {
        val g = JalaliCalendar.jalaliToGregorian(viewYear, viewMonth, 1)
        val date = LocalDate.of(g.year, g.month, g.day)
        (date.dayOfWeek.value + 1) % 7
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ---------- هدر ماه ----------
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                if (viewMonth == 1) {
                    viewMonth = 12
                    viewYear -= 1
                } else {
                    viewMonth -= 1
                }
            }) {
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = "ماه قبل",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                "${jalaliMonthName(viewMonth)} ${viewYear.toPersian()}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                if (viewMonth == 12) {
                    viewMonth = 1
                    viewYear += 1
                } else {
                    viewMonth += 1
                }
            }) {
                Icon(
                    Icons.Filled.ChevronLeft,
                    contentDescription = "ماه بعد",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        // ---------- شبکه ماه ----------
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    listOf("ش", "ی", "د", "س", "چ", "پ", "ج").forEach { d ->
                        Text(
                            d,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))

                val totalCells = firstWeekdayOffset + monthLength
                val rows = (totalCells + 6) / 7
                for (row in 0 until rows) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (col in 0 until 7) {
                            val cell = row * 7 + col
                            Box(
                                Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (cell >= firstWeekdayOffset) {
                                    val day = cell - firstWeekdayOffset + 1
                                    val isToday = day == today.day &&
                                            viewMonth == today.month &&
                                            viewYear == today.year
                                    val isSelected = day == selected.day &&
                                            viewMonth == selected.month &&
                                            viewYear == selected.year
                                    val isRed = col == 6 || Occasions.isHoliday(viewMonth, day)
                                    DayCell(
                                        day = day,
                                        isToday = isToday,
                                        isSelected = isSelected,
                                        isRed = isRed,
                                        onClick = {
                                            selected = JalaliCalendar.Jalali(viewYear, viewMonth, day)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // ---------- اطلاعات روز انتخابی ----------
        SelectedDayInfo(selected)
        Spacer(Modifier.height(24.dp))
    }
}

/** خانه یک روز در شبکه تقویم */
@Composable
private fun DayCell(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    isRed: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isToday -> MaterialTheme.colorScheme.primary
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    val textColor = when {
        isToday -> MaterialTheme.colorScheme.onPrimary
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        isRed -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(38.dp)
            .clip(CircleShape)
            .background(bgColor)
            .then(
                if (isSelected && !isToday) {
                    Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            day.toPersian(),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/** کارت اطلاعات روز انتخابی: تاریخ کامل + مناسبت‌ها */
@Composable
private fun SelectedDayInfo(day: JalaliCalendar.Jalali) {
    val gregorian = remember(day) {
        JalaliCalendar.jalaliToGregorian(day.year, day.month, day.day)
    }
    val gregorianDate = remember(gregorian) {
        LocalDate.of(gregorian.year, gregorian.month, gregorian.day)
    }
    val occasions = remember(day) { Occasions.forDate(day.month, day.day) }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "${gregorianDate.dayOfWeek.persianName()} ${day.day.toPersian()} " +
                        "${jalaliMonthName(day.month)} ${day.year.toPersian()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${gregorian.day.toPersian()} ${gregorianMonthName(gregorian.month)} " +
                        "${gregorian.year.toPersian()} میلادی",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))

            if (occasions.isEmpty()) {
                Text(
                    stringResource(R.string.no_occasion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                occasions.forEach { occasion ->
                    Row(
                        Modifier.padding(vertical = 5.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            "•",
                            color = if (occasion.isHoliday) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            occasion.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (occasion.isHoliday) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier.weight(1f)
                        )
                        if (occasion.isHoliday) {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    stringResource(R.string.official_holiday),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
