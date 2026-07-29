plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.mabrouk.recall.core.database"
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
}

dependencies {
    implementation(project(":core:model"))
    api(libs.bundles.room)
    implementation(libs.androidx.security.crypto)
    testImplementation(libs.junit)
}
