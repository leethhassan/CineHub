# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Retrofit and Moshi Models
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.squareup.moshi.* <methods>;
    @com.squareup.moshi.* <fields>;
}
-keep class com.example.data.model.** { *; }
-keep class com.example.data.remote.** { *; }

# Keep Room Database classes
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep OkHttp & Coroutines
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
