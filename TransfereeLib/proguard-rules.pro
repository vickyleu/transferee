# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\androidstuido_sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in 2build.gradle2.
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
