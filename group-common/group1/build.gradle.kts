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
    api(project(":feature-common"))
    implementation(project(":group-data:group1-data"))
}