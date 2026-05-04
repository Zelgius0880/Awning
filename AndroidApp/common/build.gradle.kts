import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    id("com.google.gms.google-services")
    alias(libs.plugins.com.google.dagger.hilt.android)
    id ("com.google.devtools.ksp")

}

android {
    namespace = "com.zelgius.awning"
    compileSdk = 36

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String","EMAIL", gradleLocalProperties(rootDir, providers).getProperty("firebase.email"))
        buildConfigField("String","PASSWORD", gradleLocalProperties(rootDir, providers).getProperty("firebase.password"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("dev"){
            initWith(getByName("debug"))

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        buildConfig = true
    }

}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)

}