plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "br.unisanta.appfirebase"
    // Mantendo 34 como a versão alvo estável
    compileSdk = 34

    defaultConfig {
        applicationId = "br.unisanta.appfirebase"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    // Dependências do Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- DEPENDÊNCIAS DO FIREBASE E FIREBASEUI ---

    // 1. Firebase Auth (para autenticação)
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")

    // 2. FirebaseUI (Telas de Login Prontas)
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")

    // 3. Firestore (Banco de dados)
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")

    // --- Fim do Firebase ---

    // Dependências de Teste
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}