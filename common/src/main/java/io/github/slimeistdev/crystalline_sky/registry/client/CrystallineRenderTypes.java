package io.github.slimeistdev.crystalline_sky.registry.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.RenderType_Duck;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.LevelRenderer_Duck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import static net.minecraft.client.renderer.RenderType.create;
import static net.minecraft.client.renderer.RenderStateShard.LIGHTMAP;

@Environment(EnvType.CLIENT)
public class CrystallineRenderTypes {
	private static final RenderStateShard.EmptyTextureStateShard SKY_TEXTURE = new SkyBufferTexture();
	private static final RenderStateShard.ShaderStateShard SKY_PROGRAM = new RenderStateShard.ShaderStateShard(CrystallineShaderInstances::getRenderTypeSkyProgram);

	public static final RenderType SKY = create(
		"crystalline_sky_sky",
		DefaultVertexFormat.BLOCK,
		VertexFormat.Mode.QUADS,
		4194304,
		true,
		false,
		RenderType.CompositeState.builder()
			.setLightmapState(LIGHTMAP)
			.setShaderState(SKY_PROGRAM)
			.setTextureState(SKY_TEXTURE)
			.createCompositeState(true)
	);

	static {
		//noinspection DataFlowIssue
		((RenderType_Duck) SKY).crystalline_sky$markAsBlockLayer();
	}

	private static class SkyBufferTexture extends RenderStateShard.EmptyTextureStateShard {
		public SkyBufferTexture() {
			super(() -> {
				Minecraft mc = Minecraft.getInstance();
				var fb = ((LevelRenderer_Duck) mc.levelRenderer).crystalline_sky$getSkyFramebuffer();
				RenderSystem.setShaderTexture(0, fb.getColorTextureId());
			}, () -> {});
		}
	}
}
