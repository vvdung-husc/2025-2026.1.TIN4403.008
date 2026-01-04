plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // ... nếu bạn đang dùng các plugins khác (ví dụ: kotlin-kapt)
}

android {
    namespace = "com.example.project1" // Đảm bảo namespace này khớp với project của bạn
    compileSdk = 34 // Hoặc phiên bản mới nhất bạn đang dùng

    defaultConfig {
        applicationId = "com.example.project1"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // AndroidX Dependencies (Khắc phục lỗi Unresolved reference 'appcompat', 'activity', 'constraintlayout')
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // API Dependency: OkHttp (Khắc phục lỗi 'okhttp3' trong ApiClient.java)
    // Tôi khuyên dùng 4.x ổn định này:
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Hoặc nếu bạn muốn dùng 5.x mới nhất:
    // implementation("com.squareup.okhttp3:okhttp:5.0.0-rc1")

    // Testing Dependencies (Khắc phục lỗi Unresolved reference 'junit', 'espresso')
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}