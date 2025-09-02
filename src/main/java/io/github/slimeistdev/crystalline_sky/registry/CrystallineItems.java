package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.content.blocks.SkyLightBlock;
import io.github.slimeistdev.crystalline_sky.content.items.WeepingSkyBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.LightBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.*;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;

public class CrystallineItems {
	public static final Item SKY = Items.register(
		CrystallineBlocks.SKY,
		settings -> settings.rarity(Rarity.EPIC)
	);

	public static final Item WEEPING_SKY = Items.register(
		CrystallineBlocks.WEEPING_SKY,
		(block, settings) -> new WeepingSkyBlockItem(block, settings.rarity(Rarity.EPIC))
	);

	public static final Item SKY_LIGHT = Items.register(
		CrystallineBlocks.SKY_LIGHT,
		settings -> settings.rarity(Rarity.EPIC)
			.component(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(LightBlock.LEVEL_15, 15))
	);

	public static final Item WEEPING_SKY_LIGHT = Items.register(
		CrystallineBlocks.WEEPING_SKY_LIGHT,
		settings -> settings.rarity(Rarity.EPIC)
	);

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(entries -> {
			if (!entries.getContext().hasPermissions()) return;

			List<ItemStack> stacks = new ArrayList<>();

			stacks.add(new ItemStack(SKY));
			stacks.add(new ItemStack(WEEPING_SKY));

			for (int i = 15; i >= 0; i--) {
				stacks.add(SkyLightBlock.addNbtForLevel(new ItemStack(SKY_LIGHT), i));
			}

			stacks.add(new ItemStack(WEEPING_SKY_LIGHT));

			entries.addAfter(Items.LIGHT, stacks);
		});
	}
}
