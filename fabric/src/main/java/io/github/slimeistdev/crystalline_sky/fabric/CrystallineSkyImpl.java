package io.github.slimeistdev.crystalline_sky.fabric;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import net.fabricmc.api.ModInitializer;

public class CrystallineSkyImpl implements ModInitializer {

	@Override
	public void onInitialize() {
		CrystallineSky.init();
	}
}
