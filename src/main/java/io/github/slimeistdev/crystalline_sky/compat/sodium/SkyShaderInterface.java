package io.github.slimeistdev.crystalline_sky.compat.sodium;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformFloat3v;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformFloat4v;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformInt;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformMatrix4f;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderFogComponent;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderTextureSlot;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ShaderBindingContext;
import net.caffeinemc.mods.sodium.client.util.TextureUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL32C;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("DeprecatedIsStillUsed")
public class SkyShaderInterface implements ChunkShaderInterface {
	private final Map<ChunkShaderTextureSlot, GlUniformInt> uniformTextures;

	private final GlUniformMatrix4f uniformModelViewMatrix;
	private final GlUniformMatrix4f uniformProjectionMatrix;
	private final GlUniformFloat3v uniformRegionOffset;
	private final GlUniformFloat4v uniformColorModulator;

	// The fog shader component used by this program in order to set up the appropriate GL state
	private final ChunkShaderFogComponent fogShader;

	private final float[] colorModulator = new float[] {1.0f, 1.0f, 1.0f, 1.0f};

	public SkyShaderInterface(ShaderBindingContext context, ChunkShaderOptions options) {
		this.uniformModelViewMatrix = context.bindUniform("u_ModelViewMatrix", GlUniformMatrix4f::new);
		this.uniformProjectionMatrix = context.bindUniform("u_ProjectionMatrix", GlUniformMatrix4f::new);
		this.uniformRegionOffset = context.bindUniform("u_RegionOffset", GlUniformFloat3v::new);
		this.uniformColorModulator = context.bindUniform("u_ColorModulator", GlUniformFloat4v::new);

		this.uniformTextures = new EnumMap<>(ChunkShaderTextureSlot.class);
		this.uniformTextures.put(ChunkShaderTextureSlot.BLOCK, context.bindUniform("u_BlockTex", GlUniformInt::new));

		this.fogShader = options.fog().getFactory().apply(context);
	}

	@Override
	public void setupState() {
		this.bindTexture(ChunkShaderTextureSlot.BLOCK, TextureUtil.getBlockTextureId());

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null
			&& client.player != null
			&& (client.player.getMainHandStack().isOf(CrystallineItems.SKY)
			|| client.player.getOffHandStack().isOf(CrystallineItems.SKY)
			|| client.player.getMainHandStack().isOf(CrystallineItems.WEEPING_SKY)
			|| client.player.getOffHandStack().isOf(CrystallineItems.WEEPING_SKY))) {

			float f = client.world.getTime() + client.getRenderTickCounter().getTickDelta(true);
			float alpha = (MathHelper.sin(f / 10.0f) + 1.0f) / 2.0f;
			// remap alpha from [0, 1] to [0, 0.75]
			float maxAlpha = 1.0f - 0.25f;
			alpha = alpha * maxAlpha;
			colorModulator[3] = alpha;
		} else {
			colorModulator[3] = 1.0f;
		}
		this.uniformColorModulator.set(colorModulator);

		this.fogShader.setup();
	}

	@Override // the shader interface should not modify pipeline state
	public void resetState() {
		// This is used by alternate implementations.
	}

	/** @deprecated */
	@Deprecated(
		forRemoval = true
	)
	private void bindTexture(ChunkShaderTextureSlot slot, int textureId) {
		GlStateManager._activeTexture(GL32C.GL_TEXTURE0 + slot.ordinal());
		GlStateManager._bindTexture(textureId);
		GlUniformInt uniform = this.uniformTextures.get(slot);
		uniform.setInt(slot.ordinal());
	}

	@Override
	public void setProjectionMatrix(Matrix4fc matrix) {
		this.uniformProjectionMatrix.set(matrix);
	}

	@Override
	public void setModelViewMatrix(Matrix4fc matrix) {
		this.uniformModelViewMatrix.set(matrix);
	}

	@Override
	public void setRegionOffset(float x, float y, float z) {
		this.uniformRegionOffset.set(x, y, z);
	}
}
