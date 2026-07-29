pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Recall"

include(":app")

include(":core:model")
include(":core:designsystem")
include(":core:database")

include(":data:ai")
include(":data:remote")
include(":data:repository")

include(":feature:capture")
include(":feature:library")
include(":feature:ask")
include(":feature:lab")
