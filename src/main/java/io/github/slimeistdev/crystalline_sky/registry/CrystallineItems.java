package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.content.blocks.SkyLightBlock;
import io.github.slimeistdev.crystalline_sky.content.items.WeepingSkyBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.LightBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class CrystallineItems {
	public static final Item SKY = Items.register(
		CrystallineBlocks.SKY,
		settings -> settings.rarity(Rarity.EPIC)
	);

	public static final Item WEEPING_SKY = register(
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

	@SuppressWarnings("SameParameterValue")
	private static Item register(Block block, BiFunction<Block, Item.Settings, BlockItem> factory) {
		return Items.register(factory.apply(block, new Item.Settings()));
	}

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
