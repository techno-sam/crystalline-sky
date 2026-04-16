package io.github.slimeistdev.crystalline_sky.foundation.registration.holder;

import net.minecraft.core.HolderOwner;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;

public class DataComponentTypeHolder<T> extends BaseHolder<DataComponentType<T>> {
	public DataComponentTypeHolder(HolderOwner<DataComponentType<T>> owner, ResourceKey<DataComponentType<T>> key) {
		super(owner, key);
	}
}
