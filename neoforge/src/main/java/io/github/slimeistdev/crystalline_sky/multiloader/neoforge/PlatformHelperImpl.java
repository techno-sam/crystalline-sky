package io.github.slimeistdev.crystalline_sky.multiloader.neoforge;

import io.github.slimeistdev.crystalline_sky.annotation.multiloader.ImplClass;
import net.neoforged.fml.loading.FMLLoader;

@ImplClass
public class PlatformHelperImpl {
	public static boolean isDevEnv() {
		return !FMLLoader.isProduction();
	}
}
