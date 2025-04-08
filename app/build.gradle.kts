plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  id("com.google.devtools.ksp") version "2.1.0-1.0.29"

  kotlin("plugin.serialization") version "2.1.0"
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.gami13.musicplayer"
  compileSdk = 35
  androidResources {
    generateLocaleConfig = true
  }
  defaultConfig {
    applicationId = "com.gami13.musicplayer"
    minSdk = 33
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
      )
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  splits.abi {
    isEnable = true
    reset()
    include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
    isUniversalApk = true
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  buildFeatures {
    compose = true
  }
  buildToolsVersion = "35.0.0"
  packaging {
    jniLibs {
      useLegacyPackaging = true
    }
  }

}
ksp{
  arg("room.schemaLocation", "$projectDir/schemas")
}


val localeTask: TaskProvider<GenerateLocaleCodeEnumTask> =
  tasks.register<GenerateLocaleCodeEnumTask>("generateLanguageList") {
    resourcesDir.set(project.file("./src/main/res"))
    outputFile.set(project.file("./src/main/java/com/gami13/musicplayer/locales/LanguageList.kt"))
  }



tasks.named("preBuild").configure {
  dependsOn(localeTask)
}

dependencies {
  implementation(libs.androidx.material.icons.extended)
  implementation(libs.jetbrains.kotlinx.serialization.json)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.navigation.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.appcompat.resources)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.ktor.client.core)
  implementation(libs.ktor.client.cio)
  implementation(libs.androidx.room.runtime)
  implementation(libs.kotlinx.datetime)
  ksp(libs.androidx.room.compiler)
  implementation(libs.ytdlp.library)
  implementation(libs.ytdlp.ffmpeg)
  implementation(libs.ytdlp.aria2c)
  implementation(libs.coil.compose)
  implementation(libs.coil.network.okhttp)
  implementation(libs.androidx.documentfile)
  implementation(libs.androidx.palette.ktx)
  implementation(libs.androidx.media)
}