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
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = "sk.eyJ1IjoiYS1yYWQiLCJhIjoiY20xa3lsMXJxMDNlMzJqcXVjNHlndzRneiJ9.vvwBiWn5FuZD_8d9h4BaUQ"
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}


rootProject.name = "mobv"
include(":app")
 