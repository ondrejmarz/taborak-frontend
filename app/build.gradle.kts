plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
    id("com.google.gms.google-services")
}

android {
    namespace = "cz.ondrejmarz.taborak"
    compileSdk = 34

    defaultConfig {
        applicationId = "cz.ondrejmarz.taborak"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    val composeBom = platform("androidx.compose:compose-bom:2024.02.02")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Material Design 3
    implementation("androidx.compose.material3:material3")

    //Firebase
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")

    // OKHTTP3
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    // Navigation
    implementation("io.github.raamcosta.compose-destinations:core:1.6.15-beta")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.6.15-beta")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation("androidx.compose.material:material-icons-core")
    // Optional - Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Optional - Add window size utils
    implementation("androidx.compose.material3:material3-window-size-class")

    // Optional - Integration with activities
    implementation("androidx.activity:activity-compose:1.8.2")
    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    // Optional - Integration with RxJava
    implementation("androidx.compose.runtime:runtime-rxjava2")

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("androidx.core:core-ktx:1.9.22")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}