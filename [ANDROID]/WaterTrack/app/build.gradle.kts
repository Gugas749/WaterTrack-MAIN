plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.grupok.watertrack"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.grupok.watertrack"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1"

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
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += setOf("META-INF/NOTICE.md", "META-INF/LICENSE.md")
        }
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

    //DIMEN
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    //ROOM
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.common)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    //MAIL SENDING
    implementation(libs.android.mail)
    implementation(libs.android.activation)

    //API
    implementation(libs.volley)
    implementation(libs.gson)
}