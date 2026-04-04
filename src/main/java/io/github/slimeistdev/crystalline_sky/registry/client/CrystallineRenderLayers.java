package io.github.slimeistdev.crystalline_sky.registry.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.WorldRenderer_Duck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import static net.minecraft.client.render.RenderLayer.of;
import static net.minecraft.client.render.RenderPhase.ENABLE_LIGHTMAP;

@Environment(EnvType.CLIENT)
public class CrystallineRenderLayers {
	private static final RenderPhase.TextureBase SKY_TEXTURE = new SkyBufferTexture();
	private static final RenderPhase.ShaderProgram SKY_PROGRAM = new RenderPhase.ShaderProgram(CrystallineShaderPrograms::getRenderTypeSkyProgram);

	public static final RenderLayer SKY = of(
		"crystalline_sky_sky",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		4194304,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder()
			.lightmap(ENABLE_LIGHTMAP)
			.program(SKY_PROGRAM)
			.texture(SKY_TEXTURE)
			.build(true)
	);

	private static class SkyBufferTexture extends RenderPhase.TextureBase {
		public SkyBufferTexture() {
			super(() -> {
				MinecraftClient mc = MinecraftClient.getInstance();
				var fb = ((WorldRenderer_Duck) mc.worldRenderer).crystalline_sky$getSkyFramebuffer();
				RenderSystem.setShaderTexture(0, fb.getColorAttachment());
			}, () -> {});
		}
	}
}
