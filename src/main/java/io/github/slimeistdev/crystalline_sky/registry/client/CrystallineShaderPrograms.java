package io.github.slimeistdev.crystalline_sky.registry.client;

import net.minecraft.client.gl.ShaderProgram;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class CrystallineShaderPrograms {
	@ApiStatus.Internal
	public static @Nullable ShaderProgram renderTypeSkyProgram;

	public static @Nullable ShaderProgram getRenderTypeSkyProgram() {
		return renderTypeSkyProgram;
	}
}
