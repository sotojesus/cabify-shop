import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

val javaVersion = project.extra.get("javaVersion") as JavaVersion
val kotlinJvmTarget = project.extra.get("kotlinJvmTarget").toString()

android {
    namespace = "com.jesussoto.android.cabifyshop"
    compileSdk = extra.get("compileSdkVersion") as Int

    defaultConfig {
        applicationId = "com.jesussoto.android.cabifyshop"
        minSdk = extra.get("minSdkVersion") as Int
        targetSdk = extra.get("targetSdkVersion") as Int
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
         jvmTarget = kotlinJvmTarget
    }
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":data"))
//    implementation(project(":domain"))

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.recyclerView)
    implementation(libs.material.components)
    implementation(libs.retrofit.core) // TODO: try to remove this
    implementation(libs.glide)

    implementation(libs.rxjava.core)
    implementation(libs.rxandroid)
    implementation(libs.androidx.work)
    implementation(libs.androidx.work.rxjava)

    implementation(libs.dagger.core)
    kapt(libs.dagger.compiler)
    implementation(libs.bundles.dagger.android)
    kapt(libs.dagger.android.processor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = kotlinJvmTarget
    }
}
