package io.github.slimeistdev.crystalline_sky.fabric.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineAtlases;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Locale;
import java.util.Optional;

import static net.minecraft.data.models.model.TextureMapping.getBlockTexture;
import static net.minecraft.data.models.model.TexturedModel.createDefault;

@SuppressWarnings("SameParameterValue")
public class CrystallineSkyModelProvider extends FabricModelProvider {
	private static ResourceLocation getSkyboxBlockTexture(Block block) {
		ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(block);
		return resourceLocation.withPrefix("crystalline_sky_skybox/");
	}

	private static ModelTemplate create(String blockModelLocation, TextureSlot... requiredSlots) {
		return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/" + blockModelLocation)), Optional.empty(), requiredSlots);
	}

	private static final ModelTemplate CUBE_ALL_PARTICLE = create("cube_all", TextureSlot.ALL, TextureSlot.PARTICLE);

	private static final TexturedModel.Provider SKYBOX_CUBE = createDefault(
		block -> TextureMapping.cube(getSkyboxBlockTexture(block))
			.put(TextureSlot.PARTICLE, getBlockTexture(block)),
		CUBE_ALL_PARTICLE
	);

	public CrystallineSkyModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators gen) {
		registerSkyBlock(gen, CrystallineBlocks.SKY.value());
		registerSkyBlock(gen, CrystallineBlocks.WEEPING_SKY.value());
		registerSkyLightBlock(gen, CrystallineBlocks.SKY_LIGHT.value(), CrystallineItems.SKY_LIGHT.value());

		gen.createAirLikeBlock(CrystallineBlocks.WEEPING_SKY_LIGHT.value(), CrystallineItems.WEEPING_SKY_LIGHT.value());
		gen.createSimpleFlatItemModel(CrystallineItems.WEEPING_SKY_LIGHT.value());

		registerSkyboxBlock(gen, CrystallineBlocks.SKYBOX_TEST.value());
		registerSkyboxBlock(gen, CrystallineBlocks.SKYBOX_SUNNY_DAY.value());
	}

	private void registerSkyBlock(BlockModelGenerators gen, Block block) {
		gen.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, TexturedModel.CUBE.create(
			block,
			(id, json) -> {
				gen.modelOutput.accept(id, () -> {
					JsonObject root = json.get().getAsJsonObject();
					root.addProperty("render_type", CrystallineSky.id("sky").toString());
					return root;
				});
			}
		)));
		gen.createSimpleFlatItemModel(block);
	}

	private void registerSkyboxBlock(BlockModelGenerators gen, Block block) {
		gen.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, SKYBOX_CUBE.create(
			block,
			(id, json) -> {
				gen.modelOutput.accept(id, () -> {
					JsonObject root = json.get().getAsJsonObject();
					root.addProperty("render_type", CrystallineSky.id("skybox").toString());
					root.addProperty(CrystallineSky.id("atlas").toString(), CrystallineAtlases.SKYBOXES.texture.toString());
					root.addProperty("loader", CrystallineSky.id("skybox").toString());
					return root;
				});
			}
		)));
		gen.createSimpleFlatItemModel(block);
	}

	private void registerSkyLightBlock(BlockModelGenerators gen, Block block, Item item) {
		gen.skipAutoItemBlock(block);
		PropertyDispatch.C1<Integer> singleProperty = PropertyDispatch.property(BlockStateProperties.LEVEL);

		for (int level = 0; level <= 15; level++) {
			String suffix = String.format(Locale.ROOT, "_%02d", level);
			ResourceLocation texture = TextureMapping.getItemTexture(item, suffix);

			singleProperty.select(level, Variant.variant().with(
				VariantProperties.MODEL,
				ModelTemplates.PARTICLE_ONLY.createWithSuffix(block, suffix, TextureMapping.particle(texture), gen.modelOutput))
			);
			ModelTemplates.FLAT_ITEM.create(
				ModelLocationUtils.getModelLocation(item, suffix),
				TextureMapping.layer0(texture),
				gen.modelOutput
			);
		}

		ModelTemplates.FLAT_ITEM.create(
			ModelLocationUtils.getModelLocation(item),
			TextureMapping.layer0(item),
			gen.modelOutput,
			(id, textures) -> {
				JsonObject base = ModelTemplates.FLAT_ITEM.createBaseTemplate(id, textures);
				var overrides = new JsonArray();
				for (int level = 0; level <= 15; level++) {
					JsonObject override = new JsonObject();

					JsonObject predicate = new JsonObject();
					predicate.addProperty("level", level / 16f);
					override.add("predicate", predicate);

					String suffix = String.format(Locale.ROOT, "_%02d", level);
					ResourceLocation model = TextureMapping.getItemTexture(item, suffix);
					override.addProperty("model", model.toString());

					overrides.add(override);
				}
				base.add("overrides", overrides);
				return base;
			});
		gen.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(singleProperty));
	}

	@Override
	public void generateItemModels(ItemModelGenerators gen) {}
}
