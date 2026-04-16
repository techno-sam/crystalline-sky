package io.github.slimeistdev.crystalline_sky.foundation.registration;

import io.github.slimeistdev.crystalline_sky.foundation.registration.holder.BaseHolder;
import io.github.slimeistdev.crystalline_sky.mixin.Holder$ReferenceAccessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record Registration<R, T extends R, H extends BaseHolder<T>>(
	ResourceLocation id,
	Registry<R> registry,
	Supplier<T> value,
	H holder,
	Consumer<H> afterRegisterCallback
) {
	@SuppressWarnings("unchecked")
	public void register() {
		T registered = Registry.register(registry, id, value.get());
		((Holder$ReferenceAccessor<T>) holder).crystalline_sky$bindValue(registered);
		afterRegisterCallback.accept(holder);
	}
}
