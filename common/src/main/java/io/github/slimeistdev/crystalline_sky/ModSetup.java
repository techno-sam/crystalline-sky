package io.github.slimeistdev.crystalline_sky;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineDataComponentTypes;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;

public class ModSetup {
	public static void init() {
		CrystallineDataComponentTypes.init();
		CrystallineItems.init();
		CrystallineBlocks.init();
	}
}
