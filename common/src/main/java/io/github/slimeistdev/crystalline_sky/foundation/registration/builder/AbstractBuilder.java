package io.github.slimeistdev.crystalline_sky.foundation.registration.builder;

import io.github.slimeistdev.crystalline_sky.foundation.registration.CatnipRegistry;
import io.github.slimeistdev.crystalline_sky.foundation.registration.Registration;
import io.github.slimeistdev.crystalline_sky.foundation.registration.holder.BaseHolder;
import io.github.slimeistdev.crystalline_sky.multiloader.Loader;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class AbstractBuilder<R, T extends R, H extends BaseHolder<T>> {
	protected final CatnipRegistry owner;
	protected final ResourceLocation id;
	protected final Registry<R> registry;
	protected @Nullable H holder;
	private Consumer<H> afterRegisterCallback = h -> h.getInternalRegistrationListener().accept(h.value());

	public AbstractBuilder(CatnipRegistry owner, String name, Registry<R> registry) {
		this.owner = owner;
		this.id = ResourceLocation.fromNamespaceAndPath(owner.modId, name);
		this.registry = registry;
	}

	public void chainAfterRegisterCallback(Consumer<H> callback) {
		afterRegisterCallback = afterRegisterCallback.andThen(callback);
	}

	abstract T build();

	abstract H getHolder(HolderOwner<R> owner, ResourceKey<R> key);

	public H register() {
		ResourceKey<R> key = ResourceKey.create(registry.key(), id);
		holder = getHolder(registry.holderOwner(), key);
		Registration<R, T, H> registration = new Registration<>(id, registry, this::build, holder, afterRegisterCallback);

		if (Loader.FABRIC.isCurrent()) {
			registration.register();
		} else {
			CatnipRegistry.addToRegistrationMap(registry, registration);
		}

		return holder;
	}
}
