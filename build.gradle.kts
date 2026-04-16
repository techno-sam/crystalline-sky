import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.modrinth.minotaur.ModrinthExtension
import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.libs

plugins {
	java
	`maven-publish`
	alias(libs.plugins.arch)
	alias(libs.plugins.loom) apply false
	alias(libs.plugins.shadow) apply false
	alias(libs.plugins.minotaur) apply false
}

println("Crystalline Sky v${"modVersion"()}")

val isRelease = System.getenv("RELEASE_BUILD")?.toBoolean() ?: false
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toInt()
val inCI = buildNumber != null
val gitHash = "\"${calculateGitHash() + (if (hasUnstaged()) "-modified" else "")}\""

extra["inCI"] = inCI

architectury {
	minecraft = libs.versions.mc.get()
}

allprojects {
	val libs = rootProject.libs

	apply(plugin = "java")
	apply(plugin = libs.plugins.arch.get().pluginId)
	apply(plugin = "maven-publish")

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
		withSourcesJar()
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}

	base.archivesName.set("slug"())
	group = "mavenGroup"()

	// Formats the mod version to include the loader, Minecraft version, and build number (if present)
	// example: 1.0.0+fabric-1.19.2-build.100 (or -local)
	val build = buildNumber?.let { "-build.${it}" } ?: "-local"

	var gitBranchLabel = "";
	if (!isRelease && "modVersion"().endsWith("-alpha")) {
		// gitBranchLabel should be "-" + the current git branch (replacing any slashes with underscores)
		gitBranchLabel = "-" + calculateGitBranch().replace("/", "_")
	}

	version = "${"modVersion"()}${gitBranchLabel}+${project.name}-mc${libs.versions.mc.get()}${if (isRelease) "" else build}"

	tasks.withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		options.release = 21
	}
}

subprojects {
	val libs = rootProject.libs

	apply(plugin = libs.plugins.loom.get().pluginId)

	setupRepositories()

	val capitalizedName = {
		if (project.name == "neoforge") {
			"NeoForge"
		} else {
			project.name.replaceFirstChar { it.titlecase() }
		}
	}();

	val loom = project.extensions.getByType<LoomGradleExtensionAPI>()
	loom.apply {
		silentMojangMappingsLicense()
		runs.configureEach {
			vmArg("-XX:+AllowEnhancedClassRedefinition")
			vmArg("-XX:+IgnoreUnrecognizedVMOptions")
			vmArg("-Dmixin.debug.export=true")
			vmArg("-Dmixin.env.remapRefMap=true")
			vmArg("-Dmixin.env.refMapRemappingFile=${projectDir}/build/createSrgToMcp/output.srg")
		}
	}

	configurations.configureEach {
		resolutionStrategy {
			force(libs.fl)
		}
	}

	@Suppress("UnstableApiUsage")
	dependencies {
		"minecraft"(libs.mc)
		"mappings"(loom.layered {
			officialMojangMappings { nameSyntheticMembers = false }
			parchment(variantOf(libs.parchment) { artifactType("zip") })
			mappings(rootProject.file("custom_mappings.tiny"))
		})
	}

	// from here down is platform configuration
	if(project.path == ":common") {
		return@subprojects
	}

	apply(plugin = libs.plugins.shadow.get().pluginId)
	apply(plugin = libs.plugins.minotaur.get().pluginId)

	architectury {
		platformSetupLoomIde()
	}

	val common: Configuration by configurations.creating
	val shadowCommon: Configuration by configurations.creating
	val development = configurations.maybeCreate("development${capitalizedName}")

	configurations {
		compileOnly.get().extendsFrom(common)
		runtimeOnly.get().extendsFrom(common)
		development.extendsFrom(common)
	}

	dependencies {
		common(project(":common", "namedElements")) { isTransitive = false }
		shadowCommon(project(":common", "transformProduction${capitalizedName}")) { isTransitive = false }
	}

	tasks.named<ShadowJar>("shadowJar") {
		archiveClassifier = "dev-shadow"
		configurations = listOf(shadowCommon)
		exclude("architectury.common.json")
		destinationDirectory = layout.buildDirectory.dir("devlibs").get()
	}

	val remapJar = tasks.named<RemapJarTask>("remapJar") {
		from("${rootProject.projectDir}/LICENSE")
		val shadowJar = project.tasks.named<ShadowJar>("shadowJar").get()
		inputFile.set(shadowJar.archiveFile)
		injectAccessWidener = true
		dependsOn(shadowJar)
		archiveClassifier = null
	}

	tasks.processResources {
		val authors = "authors"()
		val contributors = "contributors"()
		val properties: Map<String, String> = mapOf(
			"version"        to version.toString(),
			"modId"          to "modId"(),
			"modName"        to "modName"(),
			"modDescription" to "modDescription"(),
			"homepage"       to "https://modrinth.com/mod/${"slug"()}",
			"issues"         to "https://github.com/${"user"()}/${"slug"()}/issues",
			"sources"        to "https://github.com/${"user"()}/${"slug"()}",
			"license"        to "license"(),
			"authors"        to authors.split(", ").joinToString("\",\n    \""),
			"contributors"   to contributors.split(", ").joinToString("\",\n    \""),
			"members"        to "${authors}${if (contributors.isNotBlank()) ". Contributions by ${contributors}." else ""}",
			"mc"             to "compatibleVersions"().split(", ")[0],
			"fl"             to libs.versions.fl.get(),
			"fapi"           to libs.versions.fapi.get(),
		)

		inputs.properties(properties)
		filesMatching("*.mod.json") { expand(properties) }
		filesMatching("META-INF/*mods.toml") { expand(properties) }

		// don't add development or to-do files into built jar
		exclude("**/*.bbmodel", "**/*.lnk", "**/*.xcf", "**/*.blend", "**/*.blend1")
	}

	tasks.jar {
		archiveClassifier = "dev"

		manifest {
			attributes(mapOf("Git-Hash" to gitHash))
		}
	}

	tasks.named<Jar>("sourcesJar") {
		val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
		dependsOn(commonSources)
		from(commonSources.archiveFile.map { zipTree(it) })

		manifest {
			attributes(mapOf("Git-Hash" to gitHash))
		}
	}

	components.getByName<AdhocComponentWithVariants>("java") {
		withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
			skip()
		}
	}

	val isFabric = project.name == "fabric"
	val description = providers.fileContents { rootProject.file("README.md") }.asText.map {
		"<!--DO NOT EDIT MANUALLY: synced from gh readme-->\n${it}"
	}
	configure<ModrinthExtension> {
		token = providers.environmentVariable("MODRINTH_TOKEN")
		projectId = "slug"()
		versionNumber.set(project.version.toString())
		versionType.set(providers.environmentVariable("RELEASE_TYPE").orElse("release"))
		uploadFile.set(remapJar.get().archiveFile)
		gameVersions = "compatibleVersions"().split(", ").toList()
		if (isFabric) {
			loaders.add("fabric")
			loaders.add("quilt")
		} else {
			loaders.add("neoforge")
		}
		changelog = providers.environmentVariable("CHANGELOG")
		syncBodyFrom = description
		dependencies {
			if (isFabric) {
				required.version("fabric-api", libs.versions.fapi.get())
			}
		}
	}
}

fun Project.setupRepositories() {
	repositories {
		exclusiveMaven("https://api.modrinth.com/maven", "maven.modrinth") // Axiom, Sodium
		exclusiveMaven("https://maven.terraformersmc.com/", "com.terraformersmc") // Mod Menu
		exclusiveMaven("https://maven.parchmentmc.org", "org.parchmentmc.data") // Parchment Mappings
		flatDir {
			dir("$rootDir/libs")
		}
	}
}

fun calculateGitHash(): String {
	try {
		val output = providers.exec {
			commandLine("git", "rev-parse", "HEAD")
		}
		return output.standardOutput.asText.get().trim()
	} catch(_: Throwable) {
		return "unknown"
	}
}

fun calculateGitBranch(): String {
	try {
		val output = providers.exec {
			commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
		}
		return output.standardOutput.asText.get().trim()
	} catch(_: Throwable) {
		return "unknown"
	}
}

fun hasUnstaged(): Boolean {
	try {
		val output = providers.exec {
			commandLine("git", "status", "--porcelain")
		}
		val result = output.standardOutput.asText.get().replace(Regex("M gradlew(\\.bat)?"), "").trimEnd()
		if (result.isNotEmpty())
			println("Found stageable results:\n${result}\n")
		return result.isNotEmpty()
	}  catch(_: Throwable) {
		return false
	}
}

fun Project.architectury(action: Action<ArchitectPluginExtension>) {
	action.execute(this.extensions.getByType<ArchitectPluginExtension>())
}

fun RepositoryHandler.exclusiveMaven(url: String, vararg groups: String) {
	exclusiveContent {
		forRepository { maven(url) }
		filter {
			groups.forEach {
				@Suppress("UnstableApiUsage")
				includeGroupAndSubgroups(it)
			}
		}
	}
}

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}
