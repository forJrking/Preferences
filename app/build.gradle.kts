plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.spholder"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.spholder"
//        minSdk = libs.versions.minSdk.get().toInt()
        minSdk = 23
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
}

dependencies {
    implementation(libs.gson)
    implementation(libs.mmkv)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.security)
    implementation(project("path" to ":preferences-core"))
    implementation(project("path" to ":preferences-gson"))
    implementation(project("path" to ":preferences-ktx"))
//    implementation("com.github.forJrking.Preferences:pref-ktx:2.0.0")
//    implementation("com.github.forJrking.Preferences:pref-gson:2.0.0")
//    implementation("com.github.forJrking.Preferences:pref-core:2.0.0")
    implementation("com.github.forJrking:StartActivity4Rt:1.0.0")
    testImplementation(libs.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}