package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.Framebuffer_Duck;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderTarget.class)
public class RenderTargetMixin implements Framebuffer_Duck {
	@Shadow
	protected int colorTextureId;

	@Shadow
	public int width;

	@Shadow
	public int height;

	@Shadow
	public int frameBufferId;

	@Override
	public void crystalline_sky$copyColorFrom(RenderTarget framebuffer) {
		RenderSystem.assertOnRenderThreadOrInit();
		if (this.colorTextureId == -1) {
			throw new IllegalStateException("Trying to copy color texture to a RenderTarget without a color texture");
		} else if (framebuffer.getColorTextureId() == -1) {
			throw new IllegalStateException("Trying to copy color texture from a RenderTarget without a color texture");
		} else {
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, framebuffer.frameBufferId);
			GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, this.frameBufferId);
			GlStateManager._glBlitFrameBuffer(
				0, 0, framebuffer.width, framebuffer.height,
				0, 0, this.width, this.height,
				GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST
			);
			GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, framebuffer.frameBufferId);
		}
	}
}
