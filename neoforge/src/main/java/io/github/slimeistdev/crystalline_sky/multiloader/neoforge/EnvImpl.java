package io.github.slimeistdev.crystalline_sky.multiloader.neoforge;

import io.github.slimeistdev.crystalline_sky.annotation.multiloader.ImplClass;
import io.github.slimeistdev.crystalline_sky.multiloader.Env;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus.Internal;

@ImplClass
public class EnvImpl {
	@Internal
	public static Env getCurrent() {
		return FMLEnvironment.dist == Dist.CLIENT ? Env.CLIENT : Env.SERVER;
	}
}
