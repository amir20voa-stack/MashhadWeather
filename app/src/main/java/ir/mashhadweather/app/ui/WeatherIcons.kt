package ir.mashhadweather.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import ir.mashhadweather.app.data.WeatherKind

/** انتخاب آیکون مناسب وضعیت هوا */
fun weatherIcon(kind: WeatherKind, isDay: Boolean): ImageVector = when (kind) {
    WeatherKind.CLEAR -> if (isDay) Icons.Filled.WbSunny else Icons.Filled.DarkMode
    WeatherKind.PARTLY -> Icons.Filled.FilterDrama
    WeatherKind.CLOUDY -> Icons.Filled.WbCloudy
    WeatherKind.FOG -> Icons.Filled.Dehaze
    WeatherKind.DRIZZLE -> Icons.Filled.Grain
    WeatherKind.RAIN -> Icons.Filled.Umbrella
    WeatherKind.SNOW -> Icons.Filled.AcUnit
    WeatherKind.SHOWERS -> Icons.Filled.WaterDrop
    WeatherKind.THUNDER -> Icons.Filled.Thunderstorm
}
