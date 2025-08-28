package io.github.slimeistdev.crystalline_sky.datagen.providers;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class CrystallineSkyLootProvider extends FabricBlockLootTableProvider {
	public CrystallineSkyLootProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void generate() {
		addDropWithSilkTouch(CrystallineBlocks.SKY);
		addDropWithSilkTouch(CrystallineBlocks.WEEPING_SKY);
	}
}
