package io.github.slimeistdev.crystalline_sky.mixin_ducks.client;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;

public interface DefaultFramebufferSet_Duck {
	Identifier crystalline_sky$SKY_FRAMEBUFFER = CrystallineSky.id("sky");

	Handle<Framebuffer> crystalline_sky$getSkyFramebuffer();
	void crystalline_sky$setSkyFramebuffer(Handle<Framebuffer> framebuffer);
}
