package io.github.slimeistdev.crystalline_sky.extenders_cove;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

public class CrystallineSkyEarlyRiser implements Runnable {
	@Override
	@SuppressWarnings({"Convert2MethodRef", "TrivialFunctionalExpressionUsage"})
	public void run() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			((Supplier<Runnable>) (() -> () -> CrystallineSkyEarlyRiserClient.runClient())).get().run();
		}
	}
}
