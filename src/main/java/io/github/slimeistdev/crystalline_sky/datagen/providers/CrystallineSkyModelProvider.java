package io.github.slimeistdev.crystalline_sky.datagen.providers;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.LightBlock;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static net.minecraft.client.data.BlockStateModelGenerator.createWeightedVariant;

public class CrystallineSkyModelProvider extends FabricModelProvider {
	public CrystallineSkyModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator gen) {
		gen.registerBuiltinWithParticle(CrystallineBlocks.SKY, CrystallineItems.SKY);
		gen.registerItemModel(CrystallineItems.SKY);

		registerSkyLightBlock(gen);
	}

	private void registerSkyLightBlock(BlockStateModelGenerator gen) {
		ItemModel.Unbaked fallback = ItemModels.basic(gen.uploadItemModel(CrystallineItems.SKY_LIGHT));
		Map<Integer, ItemModel.Unbaked> itemModels = new HashMap<>(16);
		BlockStateVariantMap.SingleProperty<WeightedVariant, Integer> levelProp = BlockStateVariantMap.models(Properties.LEVEL_15);

		for (int level = 0; level <= 15; level++) {
			String suffix = String.format(Locale.ROOT, "_%02d", level);
			Identifier texture = TextureMap.getSubId(CrystallineItems.SKY_LIGHT, suffix);

			levelProp.register(level, createWeightedVariant(Models.PARTICLE.upload(CrystallineBlocks.SKY_LIGHT, suffix, TextureMap.particle(texture), gen.modelCollector)));

			ItemModel.Unbaked levelItemModel = ItemModels.basic(Models.GENERATED.upload(
				ModelIds.getItemSubModelId(CrystallineItems.SKY_LIGHT, suffix),
				TextureMap.layer0(texture),
				gen.modelCollector
			));
			itemModels.put(level, levelItemModel);
		}

		gen.itemModelOutput.accept(CrystallineItems.SKY_LIGHT, ItemModels.select(LightBlock.LEVEL_15, fallback, itemModels));
		gen.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(CrystallineBlocks.SKY_LIGHT).with(levelProp));
	}

	@Override
	public void generateItemModels(ItemModelGenerator gen) {}
}
