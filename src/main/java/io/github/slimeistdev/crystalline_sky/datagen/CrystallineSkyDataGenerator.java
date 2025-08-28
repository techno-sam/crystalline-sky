package io.github.slimeistdev.crystalline_sky.datagen;

import io.github.slimeistdev.crystalline_sky.datagen.providers.CrystallineSkyLootProvider;
import io.github.slimeistdev.crystalline_sky.datagen.providers.CrystallineSkyModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CrystallineSkyDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();

		pack.addProvider(CrystallineSkyModelProvider::new);
		pack.addProvider(CrystallineSkyLootProvider::new);
	}
}
