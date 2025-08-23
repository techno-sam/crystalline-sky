package io.github.slimeistdev.crystalline_sky;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrystallineSky implements ModInitializer {
	public static final String ID = "crystalline_sky";
	public static final Logger LOG = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		ModSetup.init();
	}

	public static Identifier id(String path) {
		return Identifier.of(ID, path);
	}

	public static <T> RegistryKey<T> key(RegistryKey<? extends Registry<T>> registryKey, String path) {
		return RegistryKey.of(registryKey, id(path));
	}
}
