package io.github.slimeistdev.crystalline_sky.registry.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;

@Environment(EnvType.CLIENT)
public class CrystallineRenderPipelines {
	public static final RenderPipeline SKY_PIPELINE = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.TERRAIN_SNIPPET)
			.withFragmentShader(CrystallineSky.id("core/terrain_sky"))
			.withVertexShader(CrystallineSky.id("core/terrain_sky"))
			.withLocation(CrystallineSky.id("pipeline/sky"))
			.withShaderDefine("BAYER_BIAS", 0.03125f)
			.build()
	);
}
