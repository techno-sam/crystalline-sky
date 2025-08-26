package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.Framebuffer_Duck;
import net.minecraft.client.gl.Framebuffer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Framebuffer.class)
public class FramebufferMixin implements Framebuffer_Duck {
	@Shadow
	@Nullable
	protected GpuTexture colorAttachment;

	@Shadow
	public int textureWidth;

	@Shadow
	public int textureHeight;

	@Override
	public void crystalline_sky$copyColorFrom(Framebuffer framebuffer) {
		RenderSystem.assertOnRenderThread();
		if (this.colorAttachment == null) {
			throw new IllegalStateException("Trying to copy color texture to a RenderTarget without a color texture");
		} else if (framebuffer.getColorAttachment() == null) {
			throw new IllegalStateException("Trying to copy color texture from a RenderTarget without a color texture");
		} else {
			RenderSystem.getDevice()
				.createCommandEncoder()
				.copyTextureToTexture(framebuffer.getColorAttachment(), this.colorAttachment, 0, 0, 0, 0, 0, this.textureWidth, this.textureHeight);
		}
	}
}
