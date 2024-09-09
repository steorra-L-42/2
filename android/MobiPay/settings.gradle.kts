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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MobiPay"
include(":app")
include(":common")
include(":api")
include(":features:payment")
include(":features:cardmanagement")
include(":features:vehiclemanagement")
include(":features:memberinvitation")
include(":features:vehiclerecognition")
include(":features:auth")
