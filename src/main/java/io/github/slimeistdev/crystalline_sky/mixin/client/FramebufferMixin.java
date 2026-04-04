package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.Framebuffer_Duck;
import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Framebuffer.class)
public class FramebufferMixin implements Framebuffer_Duck {
	@Shadow
	protected int colorAttachment;

	@Shadow
	public int textureWidth;

	@Shadow
	public int textureHeight;

	@Shadow
	public int fbo;

	@Override
	public void crystalline_sky$copyColorFrom(Framebuffer framebuffer) {
		RenderSystem.assertOnRenderThreadOrInit();
		if (this.colorAttachment == -1) {
			throw new IllegalStateException("Trying to copy color texture to a RenderTarget without a color texture");
		} else if (framebuffer.getColorAttachment() == -1) {
			throw new IllegalStateException("Trying to copy color texture from a RenderTarget without a color texture");
		} else {
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, framebuffer.fbo);
			GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, this.fbo);
			GlStateManager._glBlitFrameBuffer(
				0, 0, framebuffer.textureWidth, framebuffer.textureHeight,
				0, 0, this.textureWidth, this.textureHeight,
				GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST
			);
			GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, framebuffer.fbo);
		}
	}
}
