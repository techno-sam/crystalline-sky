package io.github.slimeistdev.crystalline_sky.multiloader;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformHelper {
	@ExpectPlatform
	public static boolean isDevEnv() {
		throw new AssertionError();
	}
}
