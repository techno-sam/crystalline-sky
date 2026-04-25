package io.github.slimeistdev.crystalline_sky.compat;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.Optional;
import java.util.function.Supplier;

public enum Mods {
	SODIUM("sodium"),
	IRIS("iris"),
	AXIOM("axiom");

	public final String id;
	public final boolean isLoaded;

	Mods(String id) {
		this.id = id;
		this.isLoaded = isModLoaded(id);
	}

	@ExpectPlatform
	public static boolean isModLoaded(String id) {
		throw new AssertionError();
	}

	/**
	 * Simple hook to run code if a mod is installed
	 *
	 * @param toRun will be run only if the mod is loaded
	 * @return Optional.empty() if the mod is not loaded, otherwise an Optional of the return value of the given supplier
	 */
	public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
		if (isLoaded)
			return Optional.of(toRun.get().get());
		return Optional.empty();
	}

	/**
	 * Simple hook to execute code if a mod is installed
	 *
	 * @param toExecute will be executed only if the mod is loaded
	 */
	public void executeIfInstalled(Supplier<Runnable> toExecute) {
		if (isLoaded) {
			toExecute.get().run();
		}
	}
}
