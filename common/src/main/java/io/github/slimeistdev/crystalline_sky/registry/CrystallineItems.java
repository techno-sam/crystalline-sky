package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.content.blocks.SkyLightBlock;
import io.github.slimeistdev.crystalline_sky.content.items.WeepingSkyBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class CrystallineItems {
	public static final Item SKY = Items.registerBlock(
		CrystallineBlocks.SKY,
		settings -> settings.rarity(Rarity.EPIC)
	);

	public static final Item WEEPING_SKY = register(
		CrystallineBlocks.WEEPING_SKY,
		(block, settings) -> new WeepingSkyBlockItem(block, settings.rarity(Rarity.EPIC))
	);

	public static final Item SKY_LIGHT = Items.registerBlock(
		CrystallineBlocks.SKY_LIGHT,
		settings -> settings.rarity(Rarity.EPIC)
			.component(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(LightBlock.LEVEL, 15))
	);

	public static final Item WEEPING_SKY_LIGHT = Items.registerBlock(
		CrystallineBlocks.WEEPING_SKY_LIGHT,
		settings -> settings.rarity(Rarity.EPIC)
	);

	@SuppressWarnings("SameParameterValue")
	private static Item register(Block block, BiFunction<Block, Item.Properties, BlockItem> factory) {
		return Items.registerBlock(factory.apply(block, new Item.Properties()));
	}

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(entries -> {
			if (!entries.getContext().hasPermissions()) return;

			List<ItemStack> stacks = new ArrayList<>();

			stacks.add(new ItemStack(SKY));
			stacks.add(new ItemStack(WEEPING_SKY));

			for (int i = 15; i >= 0; i--) {
				stacks.add(SkyLightBlock.setLightOnStack(new ItemStack(SKY_LIGHT), i));
			}

			stacks.add(new ItemStack(WEEPING_SKY_LIGHT));

			entries.addAfter(Items.LIGHT, stacks);
		});
	}
}
