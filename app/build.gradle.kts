plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.eaydin79.brick"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.eaydin79.brick"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1_0_${System.currentTimeMillis()/3600000}"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    applicationVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "Brick_${versionName}.apk"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

dependencies {
    implementation(libs.androidx.annotation.jvm)
}