plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.mabrouk.recall.data.ai"
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


    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.work.runtime.ktx)

    // On-device AI stack
    implementation(libs.mlkit.text.recognition)
    implementation(libs.mlkit.image.labeling)
    implementation(libs.bundles.litert)
    implementation(libs.mediapipe.tasks.text)
    implementation(libs.bundles.camerax)

    testImplementation(libs.junit)
}
