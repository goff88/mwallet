pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "MWallet"

include(":app")

// Services
include(":services:core")
include(":services:ui-kit")
include(":services:data")
include(":services:network")
include(":services:navigation")

// API
include(":api-qr-scanner")

// Features
include(":features:feature-pin-pad")
include(":features:feature-create-account")
include(":features:feature-sign-in")
include(":features:feature-account")
include(":features:feature-my-qr")
include(":features:feature-qr-scanner")
include(":features:feature-send-money")
include(":features:feature-account-history")
