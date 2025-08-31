package io.github.slimeistdev.crystalline_sky.mixin_ducks.client;

import io.github.slimeistdev.crystalline_sky.infrastructure.client.WeepingSkyDebugRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface DebugRenderer_Duck {
	WeepingSkyDebugRenderer crystalline_sky$getWeepingSkyDebugRenderer();

	int crystalline_sky$toggleWeepingSky();
}
