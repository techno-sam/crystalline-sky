architectury {
	common {
		for (p in rootProject.subprojects) {
			if (p != project) {
				this@common.add(p.name)
			}
		}
	}
}

dependencies {
	// We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
	// Do NOT use other classes from fabric loader
	modImplementation(libs.fl)

	// required for proper remapping and compiling
	modCompileOnly(libs.fapi) // TODO: try removing this and seeing if that breaks stuff

	modCompileOnly(libs.sodium.fabric)

	compileOnly(annotationProcessor(libs.mixinextras.common.get())!!)!!
}

sourceSets.main {
	resources { // include generated resources in resources
		srcDir("src/generated/resources")
		exclude(".cache/**")
	}
}
