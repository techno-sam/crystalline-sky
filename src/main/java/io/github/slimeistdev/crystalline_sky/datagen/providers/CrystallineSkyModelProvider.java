package io.github.slimeistdev.crystalline_sky.datagen.providers;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.Locale;

//import static net.minecraft.client.data.BlockStateModelGenerator.createWeightedVariant;

@SuppressWarnings("SameParameterValue")
public class CrystallineSkyModelProvider extends FabricModelProvider {
	public CrystallineSkyModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator gen) {
		registerSkyBlock(gen, CrystallineBlocks.SKY);
		registerSkyBlock(gen, CrystallineBlocks.WEEPING_SKY);
		registerSkyLightBlock(gen, CrystallineBlocks.SKY_LIGHT, CrystallineItems.SKY_LIGHT);

		gen.registerBuiltinWithParticle(CrystallineBlocks.WEEPING_SKY_LIGHT, CrystallineItems.WEEPING_SKY_LIGHT);
		gen.registerItemModel(CrystallineItems.WEEPING_SKY_LIGHT);
	}

	private void registerSkyBlock(BlockStateModelGenerator gen, Block block) {
		gen.registerSimpleCubeAll(block);
		gen.registerItemModel(block);
	}

	private void registerSkyLightBlock(BlockStateModelGenerator gen, Block block, Item item) {
		BlockStateVariantMap.SingleProperty<Integer> singleProperty = BlockStateVariantMap.create(Properties.LEVEL_15);

		for (int level = 0; level <= 15; level++) {
			String suffix = String.format(Locale.ROOT, "_%02d", level);
			Identifier texture = TextureMap.getSubId(item, suffix);

			singleProperty.register(level, BlockStateVariant.create().put(
				VariantSettings.MODEL,
				Models.PARTICLE.upload(block, suffix, TextureMap.particle(texture), gen.modelCollector))
			);
			Models.GENERATED.upload(
				ModelIds.getItemSubModelId(item, suffix),
				TextureMap.layer0(texture),
				gen.modelCollector
			);
		}

		// TODO generate root-level item model itself
		gen.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(singleProperty));
	}

	@Override
	public void generateItemModels(ItemModelGenerator gen) {}
}
