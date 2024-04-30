plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":fake-lint"))

    implementation(project(":common"))
    implementation(project(":core-data"))
    implementation(project(":feature-common"))
}