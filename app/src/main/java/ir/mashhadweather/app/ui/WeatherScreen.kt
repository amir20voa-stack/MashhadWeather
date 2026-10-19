package ir.mashhadweather.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mashhadweather.app.R
import ir.mashhadweather.app.data.DailyUi
import ir.mashhadweather.app.data.ForecastResponse
import ir.mashhadweather.app.data.HourUi
import ir.mashhadweather.app.data.WeatherCodes
import ir.mashhadweather.app.data.WeatherUi
import ir.mashhadweather.app.data.toWeatherUi
import ir.mashhadweather.app.ui.theme.PrecipBlue
import ir.mashhadweather.app.ui.theme.SkyDayBottom
import ir.mashhadweather.app.ui.theme.SkyDayTop
import ir.mashhadweather.app.ui.theme.SkyNightBottom
import ir.mashhadweather.app.ui.theme.SkyNightTop
import ir.mashhadweather.app.ui.theme.TranslucentWhite
import ir.mashhadweather.app.ui.theme.White70
import ir.mashhadweather.app.ui.theme.White90
import ir.mashhadweather.app.util.jalaliMonthName
import ir.mashhadweather.app.util.persianName
import ir.mashhadweather.app.util.toPersian
import ir.mashhadweather.app.util.toPersianDigits
import ir.mashhadweather.app.util.toTemp
import ir.mashhadweather.app.util.windDirectionName

/** صفحه آب‌وهوا: حالت بارگذاری / خطا / محتوا */
@Composable
fun WeatherScreen(state: WeatherUiState, onRefresh: () -> Unit) {
    val forecast = state.forecast
    when {
        state.isLoading -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        forecast != null -> WeatherContent(forecast = forecast, isRefreshing = state.isRefreshing)
        else -> ErrorContent(message = state.error ?: "", onRetry = onRefresh)
    }
}

@Composable
private fun WeatherContent(forecast: ForecastResponse, isRefreshing: Boolean) {
    val ui = remember(forecast) { forecast.toWeatherUi() }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (isRefreshing) {
            LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            )
        }
        Column(Modifier.padding(16.dp)) {
            CurrentWeatherCard(ui)
            Spacer(Modifier.height(20.dp))
            SectionTitle(stringResource(R.string.section_hourly))
            HourlyRow(ui.hourly)
            Spacer(Modifier.height(20.dp))
            SectionTitle(stringResource(R.string.section_daily))
            DailyList(ui.daily)
            Spacer(Modifier.height(20.dp))
            SunTimesCard(ui.sunrise, ui.sunset)
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ==================== کارت آب‌وهوای فعلی ====================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CurrentWeatherCard(ui: WeatherUi) {
    val gradient = Brush.verticalGradient(
        colors = if (ui.isDay) {
            listOf(SkyDayTop, SkyDayBottom)
        } else {
            listOf(SkyNightTop, SkyNightBottom)
        }
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(gradient)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.location_mashhad),
                    color = White90,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    ui.todayFullDate,
                    color = White70,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = weatherIcon(WeatherCodes.kind(ui.weatherCode), ui.isDay),
                contentDescription = ui.description,
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                ui.currentTemp.toTemp(),
                color = Color.White,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 78.sp
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.padding(bottom = 14.dp)) {
                Text(
                    ui.description,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "احساس واقعی ${ui.feelsLike.toTemp()}",
                    color = White70,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "بیشینه ${ui.todayMax.toTemp()}   •   کمینه ${ui.todayMin.toTemp()}",
            color = White90,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailChip("رطوبت", "${ui.humidity.toPersian()}٪")
            DetailChip("سرعت باد", "${ui.windSpeed.toPersian()} کیلومتر/ساعت")
            DetailChip("جهت باد", "از ${windDirectionName(ui.windDirection)}")
            DetailChip("فشار هوا", "${ui.pressure.toPersian()} هکتوپاسکال")
            DetailChip("پوشش ابر", "${ui.cloudCover.toPersian()}٪")
        }
    }
}

@Composable
private fun DetailChip(label: String, value: String) {
    Column(
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(TranslucentWhite)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            color = White70,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            value,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ==================== پیش‌بینی ساعتی ====================

@Composable
private fun HourlyRow(hours: List<HourUi>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        itemsIndexed(hours) { index, hour ->
            val label = if (index == 0) {
                "الان"
            } else {
                "${hour.time.hour.toPersian()}:۰۰"
            }
            HourItem(hour = hour, label = label)
        }
    }
}

@Composable
private fun HourItem(hour: HourUi, label: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            Modifier
                .width(68.dp)
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Icon(
                imageVector = weatherIcon(
                    WeatherCodes.kind(hour.weatherCode),
                    // تخمین روز: ساعت ۶ تا ۱۹
                    isDay = hour.time.hour in 6..19
                ),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                hour.temperature.toTemp(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            if (hour.precipitationProbability > 0) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(11.dp),
                        tint = PrecipBlue
                    )
                    Text(
                        "٪${hour.precipitationProbability.toPersian()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrecipBlue
                    )
                }
            }
        }
    }
}

// ==================== پیش‌بینی روزانه ====================

@Composable
private fun DailyList(days: List<DailyUi>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        days.forEachIndexed { index, day ->
            DailyItem(day = day, index = index)
        }
    }
}

@Composable
private fun DailyItem(day: DailyUi, index: Int) {
    val dayLabel = when (index) {
        0 -> "امروز"
        1 -> "فردا"
        else -> day.date.dayOfWeek.persianName()
    }
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    dayLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${day.jalali.day.toPersian()} ${jalaliMonthName(day.jalali.month)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = weatherIcon(WeatherCodes.kind(day.weatherCode), isDay = true),
                contentDescription = WeatherCodes.label(day.weatherCode),
                modifier = Modifier.size(26.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(6.dp))
            if (day.precipitationProbability > 0) {
                Text(
                    "٪${day.precipitationProbability.toPersian()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrecipBlue
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(
                day.min.toTemp(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                " / ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                day.max.toTemp(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==================== طلوع و غروب ====================

@Composable
private fun SunTimesCard(sunrise: String, sunset: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SunCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.WbTwilight,
            label = stringResource(R.string.sunrise_label),
            time = sunrise
        )
        SunCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Bedtime,
            label = stringResource(R.string.sunset_label),
            time = sunset
        )
    }
}

@Composable
private fun SunCard(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, time: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    time.toPersianDigits(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==================== اجزای مشترک ====================

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "خطا در دریافت اطلاعات",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}
