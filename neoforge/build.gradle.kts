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

	modCompileOnly(libs.sodium.neoforge)
	/*if (!inCI && "enable_sodium"().toBoolean()) { // this doesn't work, because of jar-in-jar
		modLocalRuntime(libs.sodium.neoforge)
	}*/

	compileOnly(annotationProcessor(libs.mixinextras.common.get())!!)!!
	implementation(include(libs.mixinextras.neoforge.get())!!)!!
}

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}
