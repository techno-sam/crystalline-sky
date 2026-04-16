package io.github.slimeistdev.crystalline_sky.foundation.registration.holder;

import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ItemHolder<T extends Item> extends ItemLikeHolder<T> {
	public ItemHolder(HolderOwner<T> owner, ResourceKey<T> key) {
		super(owner, key);
	}

	@Override
	public boolean is(Item item) {
		return asItem() == item;
	}
}
