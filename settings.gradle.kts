pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.library") version "8.13.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "libxposed-api"

include(":api")
