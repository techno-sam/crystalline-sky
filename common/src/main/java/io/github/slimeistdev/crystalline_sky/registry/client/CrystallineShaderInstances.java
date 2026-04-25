package io.github.slimeistdev.crystalline_sky.registry.client;

import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class CrystallineShaderInstances {
	@ApiStatus.Internal
	public static @Nullable ShaderInstance renderTypeSkyProgram;

	@ApiStatus.Internal
	public static @Nullable ShaderInstance renderTypeSkyboxProgram;

	public static @Nullable ShaderInstance getRenderTypeSkyProgram() {
		return renderTypeSkyProgram;
	}

	public static @Nullable ShaderInstance getRenderTypeSkyboxProgram() {
		return renderTypeSkyboxProgram;
	}
}
