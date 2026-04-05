package io.github.slimeistdev.crystalline_sky.fabric;

import io.github.slimeistdev.crystalline_sky.CrystallineSkyClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CrystallineSkyClientImpl implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CrystallineSkyClient.init();
	}
}
