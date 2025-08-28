package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import net.minecraft.block.*;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;

import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public class CrystallineBlocks {
	public static final Block SKY = register("sky", TransparentBlock::new, Settings.create()
		.strength(0.3f)
		.sounds(BlockSoundGroup.GLASS)
		.nonOpaque()
		.allowsSpawning(Blocks::never)
		.solidBlock(Blocks::never)
		.suffocates(Blocks::never)
		.blockVision(Blocks::never)
		.emissiveLighting(Blocks::always));

	public static final Block WEEPING_SKY = register("weeping_sky", TransparentBlock::new, Settings.copy(SKY));

	public static final Block SKY_LIGHT = register(
		"sky_light",
		LightBlock::new,
		AbstractBlock.Settings.create()
			.replaceable()
			.strength(-1.0F, 3600000.8F)
			.mapColor(state -> state.get(Properties.WATERLOGGED) ? MapColor.WATER_BLUE : MapColor.CLEAR)
			.dropsNothing()
			.nonOpaque()
	);

	public static void init() {}

	private static <T extends Block> T register(String id, Function<Settings, T> factory, Settings settings) {
		RegistryKey<Block> key = CrystallineSky.key(RegistryKeys.BLOCK, id);
		return Registry.register(Registries.BLOCK, key, factory.apply(settings.registryKey(key)));
	}

	public static boolean isCrystallineSky(BlockState state) {
		return getSkyLightLevel(state) > 0;
	}

	public static boolean isWeepingSky(BlockState state) {
		return state.isOf(WEEPING_SKY);
	}

	public static int getSkyLightLevel(BlockState state) {
		if (state.isOf(SKY_LIGHT)) {
			return state.get(LightBlock.LEVEL_15);
		} else if (state.isOf(SKY) || state.isOf(WEEPING_SKY)) {
			return 15;
		}

		return 0;
	}
}
