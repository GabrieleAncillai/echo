plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)

    alias(libs.plugins.gms)
    alias(libs.plugins.crashlytics)
    //id("maven-publish")
    //alias(libs.plugins.kotlin.jvm)
}

val gitHash = execute("git", "rev-parse", "HEAD").take(7)
val gitCount = execute("git", "rev-list", "--count", "HEAD").toInt()
val version = "2.0.$gitCount"
val group = "dev.brahmkshatriya.echo"

android {
    namespace = group
    compileSdk = 35

    defaultConfig {
        applicationId = group
        minSdk = 24
        targetSdk = 35
        versionCode = gitCount
        versionName = "v${version}_$gitHash($gitCount)"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
            )
        }
        create("nightly") {
            initWith(getByName("release"))
            applicationIdSuffix = ".nightly"
            resValue("string", "app_name", "Echo Nightly")
        }
        create("stable") {
            initWith(getByName("release"))
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }
}

dependencies {
    implementation(project(":common"))
    implementation(libs.kotlin.reflect)
    implementation(libs.bundles.androidx)
    implementation(libs.material)
    implementation(libs.bundles.paging)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.media3)
    implementation(libs.bundles.coil)

    implementation(libs.pikolo)
    implementation(libs.fadingedgelayout)
    implementation(libs.fastscroll)
    implementation(libs.kenburnsview)
    implementation(libs.nestedscrollwebview)

    debugImplementation(libs.bundles.firebase)
    "stableImplementation"(libs.bundles.firebase)
    "nightlyImplementation"(libs.bundles.firebase)
}

//publishing {
//    publications {
//        create<MavenPublication>("ReleaseAar") {
//            groupId = "dev.brahmkshatriya.echo"
//            artifactId = "app"
//            version = "1.0"
//
//            artifact("${layout.buildDirectory}/outputs/aar/${artifactId}-release.aar")
//
//            pom {
//                withXml {
//                    val dependenciesNode = asNode().appendNode("dependencies")
//                    configurations.getByName("implementation") {
//                        dependencies.forEach {
//                            val dependencyNode = dependenciesNode.appendNode("dependency")
//                            dependencyNode.appendNode("groupId", it.group)
//                            dependencyNode.appendNode("artifactId", it.name)
//                            dependencyNode.appendNode("version", it.version)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

fun execute(vararg command: String): String {
    val processBuilder = ProcessBuilder(*command)
    val hashCode = command.joinToString().hashCode().toString()
    val output = File.createTempFile(hashCode, "")
    processBuilder.redirectOutput(output)
    val process = processBuilder.start()
    process.waitFor()
    return output.readText().dropLast(1)
}
