import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

val javaVersion = project.extra.get("javaVersion") as JavaVersion
val kotlinJvmTarget = project.extra.get("kotlinJvmTarget").toString()

android {
    namespace = "com.jesussoto.android.cabibyshop.data"
    compileSdk = extra.get("compileSdkVersion") as Int

    defaultConfig {
        minSdk = extra.get("minSdkVersion") as Int
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // Ideally, the endpoint URL should be provided to the App as part of the secret that isn't
        // necessarily under source control instead of hardcoding it this way. But given the scope
        // of the project, and the fact that this URL is not a secret by any mean, I'm choosing to
        // inject it this way.
        buildConfigField("String", "SERVICE_ENDPOINT_URL", "\"https://gist.githubusercontent.com/palcalde/6c19259bd32dd6aafa327fa557859c2f/raw/ba51779474a150ee4367cda4f4ffacdcca479887/\"")
    }
    buildTypes {
        // TODO: Enable minifier.
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.rxjava.core)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.adapter.rxjava)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.bundles.room)
    kapt(libs.room.compiler)

    implementation(libs.dagger.core)
    kapt(libs.dagger.compiler)

}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = kotlinJvmTarget
    }
}
