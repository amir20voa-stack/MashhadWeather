// فایل build سطح پروژه — پلاگین‌ها را تعریف می‌کند اما اعمال نمی‌کند
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
