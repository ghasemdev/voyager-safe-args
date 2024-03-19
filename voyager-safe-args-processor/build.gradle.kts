plugins {
    alias(libs.plugins.jetbrainsKotlinJVM)
    id(libs.plugins.java.library.get().pluginId)
    id(libs.plugins.maven.publish.get().pluginId)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    // KSP -------------------------------------------------------------------------------------------
    "implementation"(libs.bundles.processing)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.parsuomash.voyager"
                artifactId = "voyager-safe-args-processor"
                version = libs.versions.voyager.safe.args.get()

                from(components["java"])
            }
        }
    }
}
