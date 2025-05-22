plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.denticaree"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.denticaree"
        minSdk = 24
        targetSdk = 35
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("com.google.firebase:firebase-ml-model-interpreter:22.0.4")
    implementation ("org.tensorflow:tensorflow-lite:2.13.0") // ou dernière version

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")


    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Required for one-shot operations (to use `ListenableFuture` from Guava Android)
    implementation("com.google.guava:guava:31.0.1-android")

    // Required for streaming operations (to use `Publisher` from Reactive Streams)
    implementation("org.reactivestreams:reactive-streams:1.0.4")

}