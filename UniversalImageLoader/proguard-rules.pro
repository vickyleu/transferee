# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in 2build.gradle2.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ProgressPieView
-keep class com.filippudak.ProgressPieView.** { *; }

# org.tukaani.xz
-keep class org.tukaani.xz.** { *; }
# javax.xml.stream
-keep class javax.xml.stream.** { *; }
# javax.lang.model
-keep class javax.lang.model.** { *; }
# com.umeng.social
-keep class com.umeng.socialize.** { *; }
# com.sina.weibo
-keep class com.sina.weibo.sdk.** { *; }
# com.google.common.base
-keep class com.google.common.base.** { *; }

# imageLoader
-dontwarn com.nostra13.universalimageloader.**
-keep class com.nostra13.universalimageloader.** { *; }
