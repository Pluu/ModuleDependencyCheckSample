buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    `kotlin-dsl`
}

repositories{
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("dependencyChecker") {
            id = "pluu.dependencyChecker"
            implementationClass = "tasks.DependencyCheckPlugin"
        }
    }
}
