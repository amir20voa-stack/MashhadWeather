# قوانین ProGuard / R8 برای انتشار Release

# --- kotlinx.serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# مدل‌های داده این پروژه
-keep,includedescriptorclasses class ir.mashhadweather.app.**$$serializer { *; }
-keepclassmembers class ir.mashhadweather.app.** {
    *** Companion;
}
-keepclasseswithmembers class ir.mashhadweather.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- OkHttp ---
-dontwarn okhttp3.**
-dontwarn okio.**
