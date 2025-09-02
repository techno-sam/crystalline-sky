package io.github.slimeistdev.crystalline_sky.datagen.providers;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class CrystallineSkyBlockTagProvider extends FabricTagProvider.BlockTagProvider {
	public CrystallineSkyBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
		valueLookupBuilder(TagKey.of(RegistryKeys.BLOCK, CrystallineSky.id("sky_light_emitters")))
			.add(CrystallineBlocks.SKY)
			.add(CrystallineBlocks.SKY_LIGHT)
			.add(CrystallineBlocks.WEEPING_SKY)
			.add(CrystallineBlocks.WEEPING_SKY_LIGHT);
	}
}
