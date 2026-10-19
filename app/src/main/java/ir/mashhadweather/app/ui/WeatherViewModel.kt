package ir.mashhadweather.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mashhadweather.app.data.ForecastResponse
import ir.mashhadweather.app.data.WeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** وضعیت رابط کاربری صفحه آب‌وهوا */
data class WeatherUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val forecast: ForecastResponse? = null,
    val error: String? = null
)

/**
 * ViewModel صفحه آب‌وهوا؛ دریافت داده در نخ ورودی (IO)
 * و انتشار وضعیت از طریق StateFlow.
 */
class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    /** دریافت دوباره داده‌ها؛ اگر داده قبلی موجود باشد صفحه خالی نمی‌شود */
    fun refresh() {
        val hasData = _state.value.forecast != null
        _state.update {
            it.copy(
                isLoading = !hasData && it.forecast == null,
                isRefreshing = hasData,
                error = null
            )
        }
        viewModelScope.launch {
            try {
                val forecast = withContext(Dispatchers.IO) { repository.fetchForecast() }
                _state.value = WeatherUiState(
                    isLoading = false,
                    isRefreshing = false,
                    forecast = forecast
                )
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _state.update {
                    if (it.forecast == null) {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = e.message ?: "خطای ناشناخته در دریافت اطلاعات"
                        )
                    } else {
                        // داده قدیمی همچنان نمایش داده می‌شود
                        it.copy(isRefreshing = false)
                    }
                }
            }
        }
    }
}
