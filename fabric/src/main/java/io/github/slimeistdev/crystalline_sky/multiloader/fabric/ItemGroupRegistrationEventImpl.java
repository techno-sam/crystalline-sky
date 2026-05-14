package io.github.slimeistdev.crystalline_sky.multiloader.fabric;

import io.github.slimeistdev.crystalline_sky.multiloader.ItemGroupRegistrationEvent;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class ItemGroupRegistrationEventImpl implements ItemGroupRegistrationEvent {
	private final ResourceKey<CreativeModeTab> tab;
	private final FabricItemGroupEntries entries;

	public ItemGroupRegistrationEventImpl(ResourceKey<CreativeModeTab> tab, FabricItemGroupEntries entries) {
		this.tab = tab;
		this.entries = entries;
	}

	@Override
	public ResourceKey<CreativeModeTab> getTab() {
		return tab;
	}

	@Override
	public boolean hasPermissions() {
		return entries.getContext().hasPermissions();
	}

	@Override
	public void addAfter(ItemLike reference, List<ItemStack> stacks) {
		entries.addAfter(reference, stacks);
	}

	@Override
	public void addAfter(ItemStack reference, List<ItemStack> stacks) {
		entries.addAfter(reference, stacks);
	}

	public static void register() {
		ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> {
			CrystallineItems.onItemGroupRegistration(new ItemGroupRegistrationEventImpl(
				BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).orElseThrow(),
				entries
			));
		});
	}
}
