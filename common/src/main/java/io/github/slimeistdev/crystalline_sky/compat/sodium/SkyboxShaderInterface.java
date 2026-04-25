package io.github.slimeistdev.crystalline_sky.compat.sodium;

import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineAtlases;
import io.github.slimeistdev.crystalline_sky.util.SharedRenderVariables;
import net.caffeinemc.mods.sodium.client.gl.device.GLRenderDevice;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformFloat2v;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformMatrix4f;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ShaderBindingContext;
import net.caffeinemc.mods.sodium.mixin.core.render.texture.TextureAtlasAccessor;
import net.minecraft.client.Minecraft;

public class SkyboxShaderInterface extends SkyShaderInterface {
	private final GlUniformMatrix4f uniformInvViewProjMatrix;
	private final GlUniformFloat2v uniformTexCoordShrink;

	public SkyboxShaderInterface(ShaderBindingContext context, ChunkShaderOptions options) {
		super(context, options);
		this.uniformInvViewProjMatrix = context.bindUniform("u_InvViewProjMatrix", GlUniformMatrix4f::new);
		this.uniformTexCoordShrink = context.bindUniformOptional("u_TexCoordShrink", GlUniformFloat2v::new);
	}

	@Override
	public void setupState() {
		super.setupState();
		this.uniformInvViewProjMatrix.set(SharedRenderVariables.invSkyboxMat);
		if (this.uniformTexCoordShrink != null) {
			TextureAtlasAccessor textureAtlas = (TextureAtlasAccessor) Minecraft.getInstance().getTextureManager().getTexture(CrystallineAtlases.SKYBOXES.texture);
			double subTexelPrecision = 1 << GLRenderDevice.INSTANCE.getSubTexelPrecisionBits();
			double subTexelOffset = 3.0517578E-5F;
			this.uniformTexCoordShrink.set(
				(float) (subTexelOffset - (double) 1.0F / (double) textureAtlas.getField_43113() / subTexelPrecision),
				(float) (subTexelOffset - (double) 1.0F / (double) textureAtlas.getField_43114() / subTexelPrecision)
			);
		}
	}
}
