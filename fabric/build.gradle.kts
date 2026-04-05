architectury.fabric()

val inCI = rootProject.extra["inCI"] as Boolean

loom {
	val common = project(":common")

	runs {
		create("datagen") {
			client()

			name = "Minecraft Data"
			vmArg("-Dfabric-api.data")
			vmArg("-Dfabric-api.datagen.output-dir=${common.file("src/generated/resources")}")
			vmArg("-Dfabric-api.datagen.modid=${"slug"()}")

			environmentVariable("DATAGEN", "TRUE")
		}
	}
}

dependencies {
	modImplementation(libs.fl)
	modImplementation(libs.fapi)

	if (!inCI) {
		modLocalRuntime(libs.mm)
	}

	modCompileOnly(libs.sodium.fabric)
	if (!inCI && "enable_sodium"().toBoolean()) {
		modLocalRuntime(libs.sodium.fabric)
	}

	modCompileOnly(libs.axiom)
	if (!inCI && "enable_axiom"().toBoolean()) {
		modLocalRuntime(libs.axiom)
		modLocalRuntime(libs.axiom.axiomclientapi)
		modLocalRuntime(libs.axiom.lattice)
		modLocalRuntime(libs.axiom.mixinconstraints)
	}

	compileOnly(annotationProcessor(libs.mixinextras.common.get())!!)!!
	implementation(include(libs.mixinextras.fabric.get())!!)!!
}

operator fun String.invoke(): String {
	return rootProject.ext[this] as? String
		?: throw IllegalStateException("Property $this is not defined")
}
