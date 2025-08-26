package io.github.slimeistdev.crystalline_sky.mixin.client;

import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DefaultFramebufferSet_Duck;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultFramebufferSet.class)
public class DefaultFramebufferSetMixin implements DefaultFramebufferSet_Duck {
	@Unique
	private Handle<Framebuffer> crystalline_sky$skyFramebuffer = Handle.empty();

	@Override
	public Handle<Framebuffer> crystalline_sky$getSkyFramebuffer() {
		return crystalline_sky$skyFramebuffer;
	}

	@Override
	public void crystalline_sky$setSkyFramebuffer(Handle<Framebuffer> framebuffer) {
		crystalline_sky$skyFramebuffer = framebuffer;
	}

	@Inject(method = "set", at = @At("HEAD"), cancellable = true)
	private void setSky(Identifier id, Handle<Framebuffer> framebuffer, CallbackInfo ci) {
		if (id.equals(crystalline_sky$SKY_FRAMEBUFFER)) {
			crystalline_sky$skyFramebuffer = framebuffer;
			ci.cancel();
		}
	}

	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	private void getSky(Identifier id, CallbackInfoReturnable<Handle<Framebuffer>> cir) {
		if (id.equals(crystalline_sky$SKY_FRAMEBUFFER)) {
			cir.setReturnValue(crystalline_sky$skyFramebuffer);
		}
	}

	@Inject(method = "clear", at = @At("HEAD"))
	private void clearSky(CallbackInfo ci) {
		crystalline_sky$skyFramebuffer = Handle.empty();
	}
}
