pluginManagement {
	repositories {
		maven { url = uri("https://maven.fabricmc.net/") }
		maven { url = uri("https://maven.architectury.dev") }
		maven { url = uri("https://maven.neoforged.net/releases") }
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			from(files("libs.versions.toml"))
		}
	}
}

include("common")
include("fabric")
include("neoforge")

rootProject.name = "crystalline-sky"
