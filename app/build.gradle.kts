plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.maghalam"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.maghalam"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.lint)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    // Navigation Compose
    implementation(libs.nav.compose)

    // Paging Compose
    implementation(libs.paging.compose)

    //LivaData-State
    implementation("androidx.compose.runtime:runtime-livedata:1.9.4")

    // Coil Compose
    implementation(libs.coil.compose)

    //coroutines =>
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")


    // Koin Compose (coKoin)
    implementation("dev.burnoo:cokoin:0.3.2")
    implementation("dev.burnoo:cokoin-android-viewmodel:0.3.2")
    implementation("dev.burnoo:cokoin-android-navigation:0.3.2")
    implementation("io.insert-koin:koin-compose:4.2.0-alpha3")
    implementation("io.insert-koin:koin-compose-viewmodel:4.2.0-alpha3")


    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // DataStore
    implementation(libs.datastore)

    // Paging Runtime
    implementation(libs.paging.runtime)

    // WorkManager
    implementation(libs.work.runtime)

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson
    implementation("com.google.code.gson:gson:2.14.0")

}