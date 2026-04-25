package io.github.slimeistdev.crystalline_sky.registry.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.LevelRenderer_Duck;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.RenderType_Duck;
import io.github.slimeistdev.crystalline_sky.util.SharedRenderVariables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public abstract class CrystallineRenderTypes extends RenderType {
	private static final RenderStateShard.EmptyTextureStateShard SKY_TEXTURE = new SkyBufferTexture();
	private static final RenderStateShard.ShaderStateShard SKY_PROGRAM = new RenderStateShard.ShaderStateShard(CrystallineShaderInstances::getRenderTypeSkyProgram);
	private static final RenderStateShard.ShaderStateShard SKYBOX_PROGRAM = new RenderStateShard.ShaderStateShard(CrystallineShaderInstances::getRenderTypeSkyboxProgram);
	public static final RenderStateShard.TextureStateShard SKYBOX_SHEET = new RenderStateShard.TextureStateShard(CrystallineAtlases.SKYBOXES.texture, false, false);
	public static final RenderStateShard.OutputStateShard SKYBOX_MATRICES = new RenderStateShard.OutputStateShard("skybox main_target", () -> {
		MAIN_TARGET.setupRenderState();
		RenderSystem.setTextureMatrix(SharedRenderVariables.invSkyboxMat);
	}, () -> {
		RenderSystem.resetTextureMatrix();
		MAIN_TARGET.clearRenderState();
	});

	public static final RenderType SKY = create(
		"crystalline_sky_sky",
		DefaultVertexFormat.BLOCK,
		VertexFormat.Mode.QUADS,
		786432,
		true,
		false,
		RenderType.CompositeState.builder()
			.setLightmapState(LIGHTMAP)
			.setShaderState(SKY_PROGRAM)
			.setTextureState(SKY_TEXTURE)
			.createCompositeState(true)
	);

	public static final RenderType SKYBOX = create(
		"crystalline_sky_skybox",
		DefaultVertexFormat.BLOCK,
		VertexFormat.Mode.QUADS,
		786432,
		true,
		false,
		RenderType.CompositeState.builder()
			.setLightmapState(LIGHTMAP)
			.setShaderState(SKYBOX_PROGRAM)
			.setTextureState(SKYBOX_SHEET)
			.setOutputState(SKYBOX_MATRICES)
			.createCompositeState(true)
	);

	static {
		//noinspection DataFlowIssue
		((RenderType_Duck) SKY).crystalline_sky$markAsBlockLayer();
		//noinspection DataFlowIssue
		((RenderType_Duck) SKYBOX).crystalline_sky$markAsBlockLayer();
	}

	private CrystallineRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
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

	public static void init() {}
}
