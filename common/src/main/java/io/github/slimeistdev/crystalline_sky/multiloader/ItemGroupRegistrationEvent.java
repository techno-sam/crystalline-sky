package io.github.slimeistdev.crystalline_sky.multiloader;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public interface ItemGroupRegistrationEvent {
	ResourceKey<CreativeModeTab> getTab();
	boolean hasPermissions();
	void addAfter(ItemLike reference, List<ItemStack> stacks);
}
