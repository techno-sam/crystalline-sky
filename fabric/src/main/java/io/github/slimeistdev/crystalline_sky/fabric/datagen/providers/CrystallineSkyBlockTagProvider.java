package io.github.slimeistdev.crystalline_sky.fabric.datagen.providers;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.foundation.registration.holder.BaseHolder;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class CrystallineSkyBlockTagProvider extends FabricTagProvider.BlockTagProvider {
	public CrystallineSkyBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	private static TagKey<Block> block(String path) {
		return TagKey.create(Registries.BLOCK, CrystallineSky.id(path));
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		builder(block("sky_light_emitters"))
			.add(CrystallineBlocks.SKY)
			.add(CrystallineBlocks.SKY_LIGHT)
			.add(CrystallineBlocks.WEEPING_SKY)
			.add(CrystallineBlocks.WEEPING_SKY_LIGHT);

		builder(block("rendering/projection"))
			.add(CrystallineBlocks.SKY)
			.add(CrystallineBlocks.WEEPING_SKY);

		builder(block("rendering/cubemap"))
			.add(CrystallineBlocks.SKYBOX_TEST)
			.add(CrystallineBlocks.SKYBOX_SUNNY_DAY);

		builder(BlockTags.CRYSTAL_SOUND_BLOCKS)
			.add(CrystallineBlocks.SKY)
			.add(CrystallineBlocks.WEEPING_SKY);
	}

	private TagBuilder<Block> builder(TagKey<Block> tag) {
		return new TagBuilder<>(getOrCreateTagBuilder(tag));
	}

	private record TagBuilder<T>(FabricTagProvider<T>.FabricTagBuilder wrapped) {
		public <R extends T> TagBuilder<T> add(Holder<R> object) {
			BaseHolder.<T>downcast(object).unwrap().map(wrapped::add, wrapped::add);
			return this;
		}
	}
}
