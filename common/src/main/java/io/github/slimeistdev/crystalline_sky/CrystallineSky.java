package io.github.slimeistdev.crystalline_sky;

import io.github.slimeistdev.crystalline_sky.foundation.registration.CatnipRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrystallineSky {
	public static final String ID = "crystalline_sky";
	public static final Logger LOG = LoggerFactory.getLogger(ID);
	private static final CatnipRegistry REGISTRY = new CatnipRegistry(ID);

	public static void init() {
		ModSetup.init();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}

	public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registryKey, String path) {
		return ResourceKey.create(registryKey, id(path));
	}

	@ApiStatus.Internal
	public static CatnipRegistry registry() {
		return REGISTRY;
	}
}
