@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories{
        mavenCentral()
        google()
        gradlePluginPortal()
    }
//    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
//        it.apply {
//            maven {
//                url = uri("https://repo.nju.edu.cn/repository/maven-public/")
//            }
//            maven {
//                url = uri("https://mirrors.cloud.tencent.com/gradle/")
//            }
//            maven {
//                url = uri("https://maven.aliyun.com/repository/public/")
//                isAllowInsecureProtocol = true
//            }
//            maven {
//                url = uri("https://developer.huawei.com/repo/")
//            }
//            mavenCentral()
//            gradlePluginPortal()
//            google {
//                content {
//                    includeGroupByRegex(".*google.*")
//                    includeGroupByRegex(".*android.*")
//                }
//            }
//        }
//    }
//    resolutionStrategy {
//        val properties = java.util.Properties()
//        properties.load(file("./gradle/libs.versions.toml").reader())
//        val kotlin = properties.getProperty("kotlin").removeSurrounding("\"")
//        eachPlugin {
//            if (requested.id.id == "embedded-kotlin") {
//                useVersion(kotlin)
//            } else if (requested.id.id == "kotlin-dsl") {
//                useVersion(kotlin)
//            }
//        }
//    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
        maven {
            url = uri("https://repo.nju.edu.cn/repository/maven-public/")
        }

//        all {
//            if (this is MavenArtifactRepository) {
//                val url = url.toString()
//                if ((url.startsWith("https://plugins.gradle.org")) || (url.startsWith("https://repo.gradle.org"))) {
//                    remove(this)
//                }
//            }
//        }
//        maven {
//            url = uri("https://repo.nju.edu.cn/repository/maven-public/")
//        }
//        maven {
//            url = uri("https://repo.nju.edu.cn/repository/maven-public/")
//        }
//        maven {
//            url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
//        }
//        maven { url = uri("https://www.jitpack.io") }
    }
}

rootProject.name = "transferee"
include(":PicassoImageLoader")
include(":UniversalImageLoader")
include(":GlideImageLoader")
include(":TransfereeLib")
if (System.getenv("JITPACK") == null) {
    include(":app")
}