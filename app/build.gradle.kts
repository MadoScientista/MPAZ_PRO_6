import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

val mpazBaseUrl = providers.gradleProperty("MPAZ_BASE_URL")
    .orElse(providers.environmentVariable("MPAZ_BASE_URL"))
    .getOrElse("http://10.0.2.2:8080/")

val mpazLocalOnly = providers.gradleProperty("MPAZ_LOCAL_ONLY")
    .orElse(providers.environmentVariable("MPAZ_LOCAL_ONLY"))
    .map(String::toBoolean)
    .getOrElse(false)

// La firma release no se versiona. El APK de release solo se genera cuando
// existe `keystore.properties` en la raíz del repositorio.
val releaseKeystoreFile = rootProject.file("keystore.properties")
val hasReleaseSigning = releaseKeystoreFile.exists()
val releaseKeystore = Properties().apply {
    if (hasReleaseSigning) {
        releaseKeystoreFile.inputStream().use(::load)
    }
}
val releaseTaskRequested = gradle.startParameter.taskNames.any {
    it.contains("Release", ignoreCase = true)
}
if (!hasReleaseSigning && releaseTaskRequested) {
    throw GradleException(
        "Falta keystore.properties para firmar el APK release. " +
            "Consulta README.md; el build debug sigue funcionando."
    )
}

android {
    namespace = "com.ccstudio.mpaz_pro_6"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ccstudio.mpaz_pro_6"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "MPAZ_BASE_URL", "\"$mpazBaseUrl\"")
        buildConfigField("boolean", "MPAZ_LOCAL_ONLY", "$mpazLocalOnly")
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(releaseKeystore.getProperty("storeFile"))
                storePassword = releaseKeystore.getProperty("storePassword")
                keyAlias = releaseKeystore.getProperty("keyAlias")
                keyPassword = releaseKeystore.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            optimization {
                enable = false
            }
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // Permite usar `java.time` (fechas ISO-8601 del backend) con `minSdk 24`.
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.androidx.datastore.preferences)
    androidTestImplementation(libs.androidx.room.testing)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}