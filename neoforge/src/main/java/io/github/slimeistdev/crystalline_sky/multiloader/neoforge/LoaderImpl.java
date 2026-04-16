package io.github.slimeistdev.crystalline_sky.multiloader.neoforge;

import io.github.slimeistdev.crystalline_sky.annotation.multiloader.ImplClass;
import io.github.slimeistdev.crystalline_sky.multiloader.Loader;
import org.jetbrains.annotations.ApiStatus.Internal;

@ImplClass
public class LoaderImpl {
	@Internal
	public static Loader getCurrent() {
		return Loader.NEOFORGE;
	}
}
