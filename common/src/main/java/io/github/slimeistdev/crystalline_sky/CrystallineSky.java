package io.github.slimeistdev.crystalline_sky;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrystallineSky implements ModInitializer {
	public static final String ID = "crystalline_sky";
	public static final Logger LOG = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		ModSetup.init();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}

	public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registryKey, String path) {
		return ResourceKey.create(registryKey, id(path));
	}
}
