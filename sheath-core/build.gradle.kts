plugins {
    kotlin("jvm")
    kotlin("kapt")
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("com.google.auto.service:auto-service:1.0")
    kapt("com.google.auto.service:auto-service:1.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
