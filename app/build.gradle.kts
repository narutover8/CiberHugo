plugins {
    id("com.android.application")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.ciberhugo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ciberhugo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}



dependencies {
    // Firebase
    implementation (platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation ("com.google.firebase:firebase-firestore:24.0.0")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    // Add the dependency for the Firebase SDK for Google Analytics
    implementation ("com.google.firebase:firebase-analytics")
    // Firebase Authentication
    implementation ("com.google.firebase:firebase-auth:21.0.1")
    // Firebase UI (opcional, para facilitar la integración de UI con Firebase)
    implementation ("com.firebaseui:firebase-ui-auth:8.0.0")

    implementation ("androidx.appcompat:appcompat:1.2.0")

    // Para el uso de funciones de hash seguras
    implementation ("com.google.crypto.tink:tink-android:1.5.0")
    // QR
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}