import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.isFile) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

val googleWebClientId = (
    localProperties.getProperty("google.web.client.id")
        ?: providers.environmentVariable("GOOGLE_WEB_CLIENT_ID").orNull
        ?: ""
).trim()

android {
    namespace = "com.guptarajat.screenactivetaskreminder"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.guptarajat.screenactivetaskreminder"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        resValue("string", "google_web_client_id", googleWebClientId)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)

    debugImplementation(libs.androidx.compose.ui.tooling)

    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
}
