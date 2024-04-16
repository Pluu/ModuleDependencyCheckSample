plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
dependencies {
    implementation(project(":common"))
    implementation(project(":feature-common"))
    implementation(project(":group-data:group1-data"))
    implementation(project(":group-common:group1"))
}
