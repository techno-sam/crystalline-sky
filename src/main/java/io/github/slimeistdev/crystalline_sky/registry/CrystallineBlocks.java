package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TransparentBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

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
		.blockVision(Blocks::never));

	public static void init() {}

	private static <T extends Block> T register(String id, Function<Settings, T> factory, Settings settings) {
		RegistryKey<Block> key = CrystallineSky.key(RegistryKeys.BLOCK, id);
		return Registry.register(Registries.BLOCK, key, factory.apply(settings.registryKey(key)));
	}
}
