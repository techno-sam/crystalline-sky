package io.github.slimeistdev.crystalline_sky.registry;

import net.minecraft.block.LightBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;

public class CrystallineItems {
	public static final Item SKY = Items.register(
		CrystallineBlocks.SKY,
		settings -> settings.rarity(Rarity.EPIC)
	);

	public static final Item SKY_LIGHT = Items.register(
		CrystallineBlocks.SKY_LIGHT,
		settings -> settings.rarity(Rarity.EPIC)
			.component(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(LightBlock.LEVEL_15, 15))
	);

	public static void init() {}
}
