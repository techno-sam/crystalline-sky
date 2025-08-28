package io.github.slimeistdev.crystalline_sky.datagen.providers;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.LightBlock;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static net.minecraft.client.data.BlockStateModelGenerator.createWeightedVariant;

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
	}

	private void registerSkyBlock(BlockStateModelGenerator gen, Block block) {
		gen.registerSimpleCubeAll(block);
		gen.registerItemModel(block);
	}

	private void registerSkyLightBlock(BlockStateModelGenerator gen, Block block, Item item) {
		ItemModel.Unbaked fallback = ItemModels.basic(gen.uploadItemModel(item));
		Map<Integer, ItemModel.Unbaked> itemModels = new HashMap<>(16);
		BlockStateVariantMap.SingleProperty<WeightedVariant, Integer> levelProp = BlockStateVariantMap.models(Properties.LEVEL_15);

		for (int level = 0; level <= 15; level++) {
			String suffix = String.format(Locale.ROOT, "_%02d", level);
			Identifier texture = TextureMap.getSubId(item, suffix);

			levelProp.register(level, createWeightedVariant(Models.PARTICLE.upload(block, suffix, TextureMap.particle(texture), gen.modelCollector)));

			ItemModel.Unbaked levelItemModel = ItemModels.basic(Models.GENERATED.upload(
				ModelIds.getItemSubModelId(item, suffix),
				TextureMap.layer0(texture),
				gen.modelCollector
			));
			itemModels.put(level, levelItemModel);
		}

		gen.itemModelOutput.accept(item, ItemModels.select(LightBlock.LEVEL_15, fallback, itemModels));
		gen.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block).with(levelProp));
	}

	@Override
	public void generateItemModels(ItemModelGenerator gen) {}
}
