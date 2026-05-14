package io.github.slimeistdev.crystalline_sky.multiloader.neoforge;

import io.github.slimeistdev.crystalline_sky.multiloader.ItemGroupRegistrationEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.List;

import static net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;

public class ItemGroupRegistrationEventImpl implements ItemGroupRegistrationEvent {
	private final BuildCreativeModeTabContentsEvent event;

	public ItemGroupRegistrationEventImpl(BuildCreativeModeTabContentsEvent event) {
		this.event = event;
	}

	@Override
	public ResourceKey<CreativeModeTab> getTab() {
		return event.getTabKey();
	}

	@Override
	public boolean hasPermissions() {
		return event.hasPermissions();
	}

	@Override
	public void addAfter(ItemLike reference, List<ItemStack> stacks) {
		// WARN: this is sort of hacky, but it should do for our current use cases
		ItemStack referenceStack = new ItemStack(reference.asItem());
		addAfter(referenceStack, stacks);
	}

	@Override
	public void addAfter(ItemStack reference, List<ItemStack> stacks) {
		for (int i = stacks.size() - 1; i >= 0; i--) {
			event.insertAfter(reference, stacks.get(i), PARENT_AND_SEARCH_TABS);
		}
	}
}
