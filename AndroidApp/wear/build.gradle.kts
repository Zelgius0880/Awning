@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.com.google.dagger.hilt.android)
    id ("com.google.devtools.ksp")

    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.zelgius.awning.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.zelgius.awning.wear"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":common"))

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.wear.material)
    implementation(libs.wear.compose)
    implementation(libs.wear.navigation.compose)
    implementation(libs.lifecycle.viewModel.ktx)
    implementation(libs.lifecycle.viewModel.compose)

    implementation(libs.hilt.android.navigation)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.tiles)
    implementation(libs.androidx.tiles.material)
    debugImplementation(libs.androidx.tiles.renderer)
    implementation( libs.horologist.compose.tools)
    implementation( libs.horologist.tiles)
    implementation(libs.guava.v3101android)

    // Kotlin
    implementation(libs.kotlinx.coroutines.guava.v160)


    // Use to fetch tiles from a tile provider in your tests
    testImplementation(libs.androidx.tiles.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}