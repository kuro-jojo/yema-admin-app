plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
    // Add the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")}

android {
    namespace = "com.kuro.yemaadmin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kuro.yemaadmin"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("androidx.compose:compose-bom:2023.03.00"))

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    // Declare the dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore-ktx")

    val navVersion = "2.5.3"

    // Java language implementation
    //noinspection GradleDependency
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    //noinspection GradleDependency
    implementation("androidx.navigation:navigation-ui:$navVersion")

    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

//    implementation("com.algolia:algoliasearch-core:3.16.5")
//    implementation("com.algolia:algoliasearch-apache:3.16.5")
}