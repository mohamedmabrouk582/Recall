plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.mabrouk.recall.core.designsystem"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.bundles.compose.ui)
    api(libs.androidx.compose.material.icons.extended)
    debugApi(libs.androidx.compose.ui.tooling)
}
