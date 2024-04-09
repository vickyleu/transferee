plugins {
    alias(libs.plugins.android.application)
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    namespace = "com.hitomi.transferimage"
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }
    signingConfigs {
        create("release") {
            storeFile = file("$rootDir/transfer.jks")
            keyAlias = "vans"
            keyPassword = "123456"
            storePassword = "123456"
        }
    }
    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "ENABLE_DEBUG", "false")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    lint {
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation(projects.transfereeLib)
    implementation(projects.glideImageLoader)
    implementation(projects.universalImageLoader)
    implementation(projects.picassoImageLoader)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.design)
    implementation(libs.base.adapter)
    implementation(libs.base.rvadapter)
    implementation(libs.utilcodex)
    implementation(libs.universal.image.loader)
    implementation(libs.glide)
    implementation(libs.picasso)
    implementation(libs.immersionbar)
    debugImplementation(libs.leakcanary.android)
}