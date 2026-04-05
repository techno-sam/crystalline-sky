package io.github.slimeistdev.crystalline_sky.fabric.datagen.providers;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class CrystallineSkyBlockTagProvider extends FabricTagProvider.BlockTagProvider {
	public CrystallineSkyBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, CrystallineSky.id("sky_light_emitters")))
			.add(CrystallineBlocks.SKY)
			.add(CrystallineBlocks.SKY_LIGHT)
			.add(CrystallineBlocks.WEEPING_SKY)
			.add(CrystallineBlocks.WEEPING_SKY_LIGHT);

		getOrCreateTagBuilder(BlockTags.CRYSTAL_SOUND_BLOCKS)
			.add(CrystallineBlocks.SKY)
			.add(CrystallineBlocks.WEEPING_SKY);
	}
}
