package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.mojang.blaze3d.textures.GpuTextureView;
import io.github.slimeistdev.crystalline_sky.extenders_cove.BlockRenderLayerExt;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DefaultFramebufferSet_Duck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.util.Handle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderLayer.class)
public class BlockRenderLayerMixin {
	@Inject(method = "getTextureView", at = @At("HEAD"), cancellable = true)
	private void shatterTheSky(CallbackInfoReturnable<GpuTextureView> cir) {
		if (((Object) this) == BlockRenderLayerExt.CRYSTALLINE_SKY_SKY) {
			MinecraftClient mc = MinecraftClient.getInstance();
			DefaultFramebufferSet framebuffers = ((WorldRendererAccessor) mc.worldRenderer).crystalline_sky$getFramebufferSet();
			Handle<Framebuffer> skyFramebuffer = ((DefaultFramebufferSet_Duck) framebuffers).crystalline_sky$getSkyFramebuffer();

			cir.setReturnValue(skyFramebuffer.get().getColorAttachmentView());
		}
	}
}
