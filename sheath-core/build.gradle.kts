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
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
