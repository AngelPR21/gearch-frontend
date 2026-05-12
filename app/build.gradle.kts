plugins {
    alias(libs.plugins.android.application)
    // Aplicamos aqui el plugin de Google Services declarado en el build.gradle raiz
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.gearch_frontend"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.gearch_frontend"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
// Firebase BOM (Bill of Materials) gestiona automaticamente las versiones de todas las librerias de Firebase
// Asi no hace falta especificar la version en cada libreria de Firebase individualmente
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
// Libreria de Firebase Cloud Messaging para recibir notificaciones push
// La version la gestiona el BOM automaticamente
    implementation("com.google.firebase:firebase-messaging")
}