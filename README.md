# آب‌وهوا و تقویم مشهد

اپلیکیشن اندروید متن‌باز برای نمایش **آب‌وهوای زنده مشهد** و **تقویم هجری شمسی** با مناسبت‌های رسمی ایران — ساخته‌شده با **Kotlin** و **Jetpack Compose**.

## امکانات

- **آب‌وهوای زنده مشهد**: دمای فعلی، احساس واقعی، رطوبت، سرعت و جهت باد، فشار هوا و پوشش ابر
- **پیش‌بینی ساعتی**: ۲۴ ساعت آینده همراه با احتمال بارش
- **پیش‌بینی روزانه**: ۷ روز آینده با دمای کمینه/بیشینه و احتمال بارش
- **طلوع و غروب آفتاب** مشهد
- **تقویم هجری شمسی**: با قابلیت جابه‌جایی بین ماه‌ها و سال‌ها
- **مناسبت‌های رسمی ایران**: نمایش مناسبت‌ها و تعطیلات رسمی بر اساس تقویم شمسی
- **تم خودکار**: روشن در روز و تیره در شب (هماهنگ با تنظیمات سیستم)
- **رابط کاربری کاملاً فارسی و راست‌چین** با اعداد فارسی

## فناوری‌ها

| مورد | ابزار |
|------|-------|
| زبان | Kotlin 2.0 |
| رابط کاربری | Jetpack Compose + Material 3 |
| معماری | MVVM (ViewModel + StateFlow) |
| شبکه | OkHttp + kotlinx.serialization |
| منبع داده آب‌وهوا | [Open-Meteo](https://open-meteo.com) (رایگان، بدون کلید API) |
| تقویم شمسی | الگوریتم جلالی (Borkowski) — پیاده‌سازی داخلی |
| حداقل اندروید | Android 8.0 (API 26) |
| نسخه هدف | Android 15 (API 35) |

## نحوه اجرا

### پیش‌نیازها

- [Android Studio](https://developer.android.com/studio) (نسخه Ladybug یا جدیدتر پیشنهاد می‌شود)
- JDK 17 (همراه Android Studio نصب می‌شود)

### مراحل

1. پروژه را کلون کنید:

```bash
git clone https://github.com/USERNAME/MashhadWeather.git
```

2. در Android Studio مسیر **File > Open** را انتخاب کرده و پوشه پروژه را باز کنید.
3. صبر کنید تا Gradle Sync کامل شود (بار اول بسته‌ها را دانلود می‌کند).
4. دکمه **Run** را بزنید یا از مسیر **Build > Build APK(s)** خروجی APK بگیرید.

## دریافت فایل نصبی (APK)

دو راه برای گرفتن APK دارید:

### راه اول: ساخت خودکار با GitHub Actions (بدون نیاز به Android Studio)

این پروژه یک workflow آماده دارد. کافی است پروژه را به گیت‌هاب پوش کنید:

1. طبق بخش «انتشار روی گیت‌هاب» پایین، پروژه را پوش کنید.
2. در صفحه مخزن، تب **Actions** را باز کنید و روی اجرای «Build APK» کلیک کنید.
3. بعد از اتمام (حدود ۵ دقیقه)، از بخش **Artifacts** فایل `MashhadWeather-debug-apk` را دانلود کنید.
4. فایل ZIP دانلودی را باز کنید و `app-debug.apk` را روی گوشی نصب کنید.

> هنگام نصب، اندروید از شما اجازه «نصب از منابع ناشناس» را می‌خواهد که طبیعی است چون APK امضای انتشار (Play Store) ندارد.

### راه دوم: ساخت در Android Studio

1. پروژه را در Android Studio باز کنید (طبق بخش قبل).
2. از منوی **Build > Build App Bundle(s) / APK(s) > Build APK(s)** استفاده کنید.
3. APK در مسیر `app/build/outputs/apk/debug/app-debug.apk` ساخته می‌شود.

### اجرای تست‌ها

صحت الگوریتم تقویم شمسی با تست واحد راستی‌آزمایی می‌شود (رفت‌وبرگشت روی ۷۳ هزار روز):

```bash
./gradlew test
```

## منبع داده

داده‌های آب‌وهوا از سرویس رایگان [Open-Meteo](https://open-meteo.com) دریافت می‌شود:

- بدون نیاز به ثبت‌نام یا کلید API
- مختصات مشهد: `36.2972N, 59.6067E`
- منطقه زمانی: `Asia/Tehran`

## ساختار پروژه

```
MashhadWeather/
├── app/src/main/java/ir/mashhadweather/app/
│   ├── MainActivity.kt              # فعالیت اصلی
│   ├── data/
│   │   ├── JalaliCalendar.kt        # الگوریتم تبدیل تاریخ شمسی/میلادی
│   │   ├── Occasions.kt             # مناسبت‌های رسمی ایران
│   │   ├── WeatherCodes.kt          # نگاشت کدهای WMO به فارسی
│   │   ├── WeatherModels.kt         # مدل‌های داده (DTO + مدل نمایشی)
│   │   └── WeatherRepository.kt     # دریافت داده از Open-Meteo
│   ├── ui/
│   │   ├── HomeScreen.kt            # صفحه اصلی و تب‌ها
│   │   ├── WeatherScreen.kt         # صفحه آب‌وهوا
│   │   ├── CalendarScreen.kt        # صفحه تقویم شمسی
│   │   ├── WeatherIcons.kt          # نگاشت وضعیت هوا به آیکون
│   │   ├── WeatherViewModel.kt      # ViewModel + StateFlow
│   │   └── theme/                   # تم روشن/تیره
│   └── util/
│       └── Persian.kt               # اعداد فارسی و نام ماه‌ها/روزها
├── app/src/test/.../JalaliCalendarTest.kt   # تست واحد تقویم
├── .github/workflows/build-apk.yml  # ساخت خودکار APK در گیت‌هاب
└── gradle/libs.versions.toml        # کاتالوگ نسخه‌ها (Version Catalog)
```

## محدودیت‌های نسخه فعلی

- مناسبت‌های مذهبی مبتنی بر **تقویم هجری قمری** (مانند عید قربان و غدیر) به دلیل جابه‌جایی سالانه، در این نسخه گنجانده نشده‌اند.
- شهر مشهد به‌صورت پیش‌فرض تنظیم شده و تغییر شهر در نسخه‌های بعدی اضافه خواهد شد.

## انتشار روی گیت‌هاب

اگر مخزن را خروجی گرفته‌اید، برای انتشار در گیت‌هاب:

```bash
cd MashhadWeather
git init
git add .
git commit -m "Initial commit: Mashhad weather and calendar app"
git branch -M main
git remote add origin https://github.com/USERNAME/MashhadWeather.git
git push -u origin main
```

## مشارکت

پیشنهادها و Pull Requestها خوش‌آمدند! برای تغییرات بزرگ، لطفاً ابتدا یک Issue باز کنید.

## مجوز

این پروژه تحت مجوز [MIT](LICENSE) منتشر شده است.

الگوریتم تقویم جلالی بر اساس کتابخانه متن‌باز [jalaali-js](https://github.com/jalaali/jalaali-js) (مجوز MIT) بازنویسی شده است.
