package io.github.slimeistdev.crystalline_sky.foundation.registration.holder;

import io.github.slimeistdev.crystalline_sky.foundation.registration.RegistrationListeners;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

public class BaseHolder<T> extends Holder.Reference<T> {
	private final RegistrationListeners<T> registrationListeners = new RegistrationListeners<>();

	protected BaseHolder(HolderOwner<T> owner, ResourceKey<T> key) {
		super(Type.STAND_ALONE, owner, key, null);
	}

	public <V> boolean is(V value) {
		return value() == value;
	}

	@ApiStatus.Internal
	public Consumer<T> getInternalRegistrationListener() {
		return registrationListeners::registered;
	}

	public void onRegistered(Consumer<? super T> consumer) {
		registrationListeners.addListener(consumer);
	}

	public static <T> Holder<T> downcast(Holder<? extends T> holder) {
		//noinspection unchecked
		return (Holder<T>) holder;
	}

	public String getRegisteredNamePath() {
		return this.unwrapKey().map((resourceKey) -> resourceKey.location().getPath()).orElse("[unregistered]");
	}
}
