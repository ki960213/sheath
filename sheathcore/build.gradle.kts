plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("maven-publish")
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("com.google.auto.service:auto-service:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")
    implementation(kotlin("reflect"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.5")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["kotlin"])
                groupId = "com.github.ki960213"
                artifactId = "sheathcore"
                version = "1.0.0"
            }
        }
    }
}
