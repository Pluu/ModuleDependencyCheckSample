pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ModuleDependencyCheckSample"
include(":app")
include(":common")
include(":core-data")
include(":feature-common")
include(":group-data:group1-data")
include(":group-common:group1")
include(":feature:group1:feature-a")
include(":feature:group1:feature-b")
include(":group-data:group2-data")
include(":feature:group2-single")
include(":feature:pluu")
