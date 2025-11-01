plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "fpt.edu.vn.vinho_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "fpt.edu.vn.vinho_app"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Room
//    val room_version = "2.8.3"
//    implementation("androidx.room:room-runtime:${room_version}")
//    annotationProcessor("androidx.room:room-compiler:$room_version")
//    implementation("androidx.room:room-rxjava3:${room_version}")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))

    // Required Firebase libraries
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In library
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Add the dependency for the Firebase AI Logic library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-ai")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Required for one-shot operations (to use `ListenableFuture` from Guava Android)
    implementation("com.google.guava:guava:31.0.1-android")

    // Required for streaming operations (to use `Publisher` from Reactive Streams)
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    implementation("com.google.android.material:material:1.11.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    // Add converter libraries as needed, e.g., for JSON parsing
    implementation ("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation ("com.squareup.retrofit2:converter-scalars:3.0.0")

    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    // It is also recommended to explicitly depend on RxJava's latest version
    implementation("io.reactivex.rxjava3:rxjava:3.1.5")

    // WorkManager
    implementation("androidx.work:work-runtime:2.9.0")

    // UI
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.material:material:1.10.0")
}