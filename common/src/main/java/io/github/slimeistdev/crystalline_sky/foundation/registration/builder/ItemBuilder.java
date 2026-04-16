package io.github.slimeistdev.crystalline_sky.foundation.registration.builder;

import io.github.slimeistdev.crystalline_sky.foundation.registration.CatnipRegistry;
import io.github.slimeistdev.crystalline_sky.foundation.registration.holder.ItemHolder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

import java.util.function.Function;

public class ItemBuilder<T extends Item> extends AbstractBuilder<Item, T, ItemHolder<T>> {
	private final Function<Properties, T> factory;
	private Function<Properties, Properties> properties = Function.identity();

	public ItemBuilder(CatnipRegistry owner, String name, Function<Properties, T> factory) {
		super(owner, name, BuiltInRegistries.ITEM);
		this.factory = factory;
	}

	public ItemBuilder<T> properties(Function<Properties, Properties> properties) {
		this.properties = this.properties.andThen(properties);
		return this;
	}

	@Override
	T build() {
		Properties properties = new Properties();
		properties = this.properties.apply(properties);
		return factory.apply(properties);
	}

	@Override
	ItemHolder<T> getHolder(HolderOwner<Item> owner, ResourceKey<Item> key) {
		//noinspection unchecked
		return (ItemHolder<T>) new ItemHolder<>(owner, key);
	}
}
