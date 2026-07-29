plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.mabrouk.recall.data.repository"
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
    implementation(project(":core:database"))
    implementation(project(":data:ai"))
    implementation(project(":data:remote"))


    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
}
