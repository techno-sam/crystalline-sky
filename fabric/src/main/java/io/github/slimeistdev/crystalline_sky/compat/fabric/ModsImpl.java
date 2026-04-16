package io.github.slimeistdev.crystalline_sky.compat.fabric;

import io.github.slimeistdev.crystalline_sky.annotation.multiloader.ImplClass;
import net.fabricmc.loader.api.FabricLoader;

@ImplClass
public class ModsImpl {
	public static boolean isModLoaded(String id) {
		return FabricLoader.getInstance().isModLoaded(id);
	}
}
