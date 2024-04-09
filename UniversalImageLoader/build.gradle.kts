import java.util.Locale

plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
}


description = "UniversalImageLoader"
group = "com.github.vickyleu.transferee"
version = "1.0.0"

// 配置发布到本地 Maven 仓库

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = project.group.toString()
                artifactId = project.name.lowercase(Locale.getDefault())
                version = project.version.toString()
                from(components["release"])
            }
        }
    }
}


android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    namespace = "com.vansz.universalimageloader"
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    publishing {
        singleVariant("release") {
            // if you don't want sources/javadoc, remove these lines
            withSourcesJar()
            withJavadocJar()
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    lint{
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        abortOnError = false
        checkReleaseBuilds = false
    }
}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(projects.transfereeLib)
    implementation(libs.universal.image.loader)
}
