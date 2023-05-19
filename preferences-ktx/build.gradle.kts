plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.forjrking.preferences"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        version = libs.versions.preference.versionCode.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release"){
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
        viewBinding = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
}

dependencies {
    compileOnly(project("path" to ":preferences-core"))
    implementation(libs.androidx.lifecycle.livedata)
    testImplementation(libs.junit4)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.findByName("release"))
                groupId = "com.github.forjrking"
                artifactId = "preferences-ktx"
                version = android.defaultConfig.versionName
            }
        }
    }
}