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
include(":feature-category1:feature-category1-a")
include(":feature-category1:feature-category1-b")
include(":feature-category1:feature-category1-common")
include(":feature-category2-single")
include(":common")
