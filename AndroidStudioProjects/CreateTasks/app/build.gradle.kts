plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp) // required for the Room compiler
}

android {
    namespace = "com.example.createtasks"
    compileSdk = 37 // required by core-ktx 1.19.1

    defaultConfig {
        applicationId = "com.example.createtasks"
        minSdk = 24
        targetSdk = 36 // can stay lower than compileSdk
        versionCode = 1
        versionName = "1.0"

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

    // Generates binding classes such as ItemTaskBinding from layout files
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ViewModel + LiveData + coroutines support
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Fragment + Activity (used by TaskListFragment / MainActivity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.activity.ktx)

    // UI used by TaskAdapter / TaskListFragment
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Base dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}