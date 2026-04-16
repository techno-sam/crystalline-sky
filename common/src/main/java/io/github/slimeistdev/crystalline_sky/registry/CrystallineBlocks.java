package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.content.blocks.SkyBlock;
import io.github.slimeistdev.crystalline_sky.content.blocks.SkyLightBlock;
import io.github.slimeistdev.crystalline_sky.content.blocks.WeepingSkyLightBlock;
import io.github.slimeistdev.crystalline_sky.foundation.registration.CatnipRegistry;
import io.github.slimeistdev.crystalline_sky.foundation.registration.holder.BlockHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

@SuppressWarnings("SameParameterValue")
public class CrystallineBlocks {
	private static final CatnipRegistry REGISTRY = CrystallineSky.registry();

	public static final BlockHolder<SkyBlock> SKY = REGISTRY.block("sky", SkyBlock::new)
		.properties(p -> p
			.strength(0.3f)
			.sound(SoundType.AMETHYST)
			.noOcclusion()
			.isValidSpawn(CrystallineBlocks::never)
			.isRedstoneConductor(CrystallineBlocks::never)
			.isSuffocating(CrystallineBlocks::never)
			.isViewBlocking(CrystallineBlocks::never)
			.emissiveRendering(CrystallineBlocks::always))
		.register();

	public static final BlockHolder<SkyBlock> WEEPING_SKY = REGISTRY.block("weeping_sky", SkyBlock::new)
		.initialProperties(SKY::value)
		.register();

	public static final BlockHolder<SkyLightBlock> SKY_LIGHT = REGISTRY.block("sky_light", SkyLightBlock::new)
		.properties(p -> p
			.replaceable()
			.strength(-1.0F, 3600000.8F)
			.mapColor(CrystallineBlocks::waterloggedColor)
			.noLootTable()
			.noOcclusion())
		.register();

	public static final BlockHolder<WeepingSkyLightBlock> WEEPING_SKY_LIGHT = REGISTRY.block("weeping_sky_light", WeepingSkyLightBlock::new)
		.initialProperties(SKY_LIGHT::value)
		.register();

	public static void init() {}

	public static boolean isCrystallineSky(BlockState state) {
		return getSkyLightLevel(state) > 0;
	}

	public static int getSkyLightLevel(BlockState state) {
		if (SKY_LIGHT.is(state)) {
			return state.getValue(LightBlock.LEVEL);
		} else if (SKY.is(state)) { // weeping sky explicitly excluded, that's handled differently
			return 15;
		}

		return 0;
	}

	public static boolean isWeepingSky(BlockState state) {
		return WEEPING_SKY.is(state) || WEEPING_SKY_LIGHT.is(state);
	}

	private static Boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
		return false;
	}

	private static Boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
		return true;
	}

	private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
		return true;
	}

	private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
		return false;
	}

	private static MapColor waterloggedColor(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? MapColor.WATER : MapColor.NONE;
	}
}
