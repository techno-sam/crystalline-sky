package io.github.slimeistdev.crystalline_sky.registry;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.content.blocks.SkyLightBlock;
import io.github.slimeistdev.crystalline_sky.content.items.WeepingSkyBlockItem;
import io.github.slimeistdev.crystalline_sky.foundation.registration.CatnipRegistry;
import io.github.slimeistdev.crystalline_sky.foundation.registration.holder.ItemHolder;
import io.github.slimeistdev.crystalline_sky.multiloader.ItemGroupRegistrationEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.LightBlock;

import java.util.ArrayList;
import java.util.List;

public class CrystallineItems {
	private static final CatnipRegistry REGISTRY = CrystallineSky.registry();

	public static final ItemHolder<BlockItem> SKY = REGISTRY.item(CrystallineBlocks.SKY)
		.properties(p -> p.rarity(Rarity.EPIC))
		.register();

	public static final ItemHolder<WeepingSkyBlockItem> WEEPING_SKY = REGISTRY.item(CrystallineBlocks.WEEPING_SKY, WeepingSkyBlockItem::new)
		.properties(p -> p.rarity(Rarity.EPIC))
		.register();

	public static final ItemHolder<BlockItem> SKY_LIGHT = REGISTRY.item(CrystallineBlocks.SKY_LIGHT)
		.properties(p -> p
			.rarity(Rarity.EPIC)
			.component(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(LightBlock.LEVEL, 15)))
		.register();

	public static final ItemHolder<BlockItem> WEEPING_SKY_LIGHT = REGISTRY.item(CrystallineBlocks.WEEPING_SKY_LIGHT)
		.properties(p -> p.rarity(Rarity.EPIC))
		.register();

	public static final ItemHolder<BlockItem> SKYBOX_TEST = REGISTRY.item(CrystallineBlocks.SKYBOX_TEST)
		.properties(p -> p.rarity(Rarity.EPIC))
		.register();

	@SuppressWarnings("SameParameterValue")
	private static ResourceKey<CreativeModeTab> tabKey(String name) {
		return ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.withDefaultNamespace(name));
	}

	public static void onItemGroupRegistration(ItemGroupRegistrationEvent event) {
		if (event.getTab().equals(tabKey("op_blocks")) && event.hasPermissions()) {
			List<ItemStack> stacks = new ArrayList<>();

			stacks.add(new ItemStack(SKY));
			stacks.add(new ItemStack(WEEPING_SKY));
			stacks.add(new ItemStack(SKYBOX_TEST));

			for (int i = 15; i >= 0; i--) {
				stacks.add(SkyLightBlock.setLightOnStack(new ItemStack(SKY_LIGHT), i));
			}

			stacks.add(new ItemStack(WEEPING_SKY_LIGHT));

			event.addAfter(Items.LIGHT, stacks);
		}
	}

	public static void init() {}

	public static boolean isSky(ItemStack stack) {
		return SKY.is(stack) || WEEPING_SKY.is(stack) || SKYBOX_TEST.is(stack);
	}
}
