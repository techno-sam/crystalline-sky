package io.github.slimeistdev.crystalline_sky.multiloader.fabric;

import io.github.slimeistdev.crystalline_sky.annotation.multiloader.ImplClass;
import io.github.slimeistdev.crystalline_sky.multiloader.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus.Internal;

@ImplClass
public class EnvImpl {
	@Internal
	public static Env getCurrent() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Env.CLIENT : Env.SERVER;
	}
}
