package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.content.blocks.SkyLightBlock;
import io.github.slimeistdev.crystalline_sky.content.blocks.WeepingSkyLightBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public class CrystallineBlocks {
	public static final Block SKY = register("sky", TransparentBlock::new, Properties.of()
		.strength(0.3f)
		.sound(SoundType.AMETHYST)
		.noOcclusion()
		.isValidSpawn(Blocks::never)
		.isRedstoneConductor(Blocks::never)
		.isSuffocating(Blocks::never)
		.isViewBlocking(Blocks::never)
		.emissiveRendering(Blocks::always));

	public static final Block WEEPING_SKY = register("weeping_sky", TransparentBlock::new, Properties.ofFullCopy(SKY));

	public static final Block SKY_LIGHT = register(
		"sky_light",
		SkyLightBlock::new,
		BlockBehaviour.Properties.of()
			.replaceable()
			.strength(-1.0F, 3600000.8F)
			.mapColor(state -> state.getValue(BlockStateProperties.WATERLOGGED) ? MapColor.WATER : MapColor.NONE)
			.noLootTable()
			.noOcclusion()
	);

	public static final Block WEEPING_SKY_LIGHT = register(
		"weeping_sky_light",
		WeepingSkyLightBlock::new,
		BlockBehaviour.Properties.of()
			.replaceable()
			.strength(-1.0F, 3600000.8F)
			.mapColor(state -> state.getValue(BlockStateProperties.WATERLOGGED) ? MapColor.WATER : MapColor.NONE)
			.noLootTable()
			.noOcclusion()
	);

	public static void init() {}

	private static <T extends Block> T register(String id, Function<Properties, T> factory, Properties settings) {
		ResourceKey<Block> key = CrystallineSky.key(Registries.BLOCK, id);
		return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(settings));
	}

	public static boolean isCrystallineSky(BlockState state) {
		return getSkyLightLevel(state) > 0;
	}

	public static int getSkyLightLevel(BlockState state) {
		if (state.is(SKY_LIGHT)) {
			return state.getValue(LightBlock.LEVEL);
		} else if (state.is(SKY)) { // weeping sky explicitly excluded, that's handled differently
			return 15;
		}

		return 0;
	}

	public static boolean isWeepingSky(BlockState state) {
		return state.is(WEEPING_SKY) || state.is(WEEPING_SKY_LIGHT);
	}
}
