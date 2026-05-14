architectury.neoForge()

val inCI = rootProject.extra["inCI"] as Boolean

loom {
	neoForge {
		runs.configureEach {
			// force proper color logs
			vmArg("-Dterminal.jline=true")
		}
	}
}

repositories {
	maven {
		name = "NeoForged"
		url = uri("https://maven.neoforged.net/releases")
	}

}

dependencies {
	neoForge(libs.nf)

	modCompileOnly(libs.ffapi.renderer) // only for sodium compat
	modCompileOnly(libs.sodium.neoforge)
	if (!inCI && "enable_sodium"().toBoolean()) { // this doesn't work, because of jar-in-jar. Sodium must be placed in the mods folder
		modLocalRuntime(libs.sodium.neoforge)
	}

	modCompileOnly(libs.iris.neoforge)
	if (!inCI && "enable_iris"().toBoolean()) { // this doesn't work, because of jar-in-jar. Iris must be placed in the mods folder
		modLocalRuntime(libs.iris.neoforge)
		/*modLocalRuntime("org.antlr:antlr4-runtime:4.13.1")
		modLocalRuntime("io.github.douira:glsl-transformer:2.0.1")
		modLocalRuntime("org.anarres:jcpp:1.4.14")*/
	}

	compileOnly(annotationProcessor(libs.mixinextras.common.get())!!)!!
	implementation(include(libs.mixinextras.neoforge.get())!!)!!
}

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}
