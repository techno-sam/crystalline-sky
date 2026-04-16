package io.github.slimeistdev.crystalline_sky.multiloader.fabric;

import io.github.slimeistdev.crystalline_sky.annotation.multiloader.ImplClass;
import net.fabricmc.loader.api.FabricLoader;

@ImplClass
public class PlatformHelperImpl {
	public static boolean isDevEnv() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}
}
