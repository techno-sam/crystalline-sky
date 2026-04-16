package io.github.slimeistdev.crystalline_sky.fabric.datagen.providers;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class CrystallineSkyLootProvider extends FabricBlockLootTableProvider {
	public CrystallineSkyLootProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void generate() {
		dropWhenSilkTouch(CrystallineBlocks.SKY.value());
		dropWhenSilkTouch(CrystallineBlocks.WEEPING_SKY.value());
	}
}
