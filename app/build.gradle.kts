plugins {
    alias(libs.plugins.android.application)
    // KSP + the Hilt plugin are needed for dependency injection
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.assignment2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.assignment2"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        // generates a binding class for each layout so I don't need findViewById everywhere
        viewBinding = true
    }
}

dependencies {
    // android ui
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)

    // viewmodel, lifecycleScope and coroutines
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // dependency injection with hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // networking: retrofit for the api calls and moshi to turn the json into kotlin objects
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin)
    // moshi-kotlin uses kotlin-reflect, this keeps it on the same version as the kotlin in AGP 9
    implementation(libs.kotlin.reflect)
    implementation(libs.okhttp.logging)
}
