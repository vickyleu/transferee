if (System.getenv("JITPACK") == null) {
    rootProject.layout.buildDirectory.set(file("${rootProject.rootDir.parentFile.parentFile.absolutePath}/buildOut"))
}
plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlin.kapt).apply(false)
    alias(libs.plugins.kotlin.jvm).apply(false)
}

allprojects {
    if (System.getenv("JITPACK") == null) {
        this.layout.buildDirectory.set(file("${rootProject.layout.buildDirectory.get().asFile.absolutePath}/${project.name}"))
    }
    configurations.all {
        resolutionStrategy {
            eachDependency {
                if (requested.group == "org.jetbrains.kotlin") {
                    useVersion(libs.versions.kotlin.get())
                } else if (requested.group == "org.jetbrains" && requested.name == "annotations") {
                    useVersion(libs.versions.annotations.get())
                }
            }
        }
    }
}