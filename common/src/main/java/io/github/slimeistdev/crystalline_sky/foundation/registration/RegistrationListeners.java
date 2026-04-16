package io.github.slimeistdev.crystalline_sky.foundation.registration;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RegistrationListeners<T> {
	private @Nullable T obj;
	private @Nullable Consumer<T> cons;

	public synchronized void registered(T obj) {
		this.obj = obj;
		if (cons != null) {
			cons.accept(obj);
			cons = null;
		}
	}

	public synchronized void addListener(Consumer<? super T> consumer) {
		if (obj != null) {
			consumer.accept(obj);
		} else if (cons == null) {
			cons = consumer::accept;
		} else {
			cons = cons.andThen(consumer);
		}
	}
}
