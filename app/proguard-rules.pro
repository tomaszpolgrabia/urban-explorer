# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/tpolgrabia/bin/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes Signature
-keepattributes InnerClasses
-dontwarn ch.qos.logback.core.net.**
-assumenosideeffects class ch.qos.logback.** { *; }
-assumenosideeffects class org.slf4j.** { *; }
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-dontwarn retrofit2.**
-dontwarn okio.**
-dontwarn com.crashlytics.**
-dontwarn okhttp3.logging.**

## New rules for EventBus 3.0.x ##
# http://greenrobot.org/eventbus/documentation/proguard/

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}