package io.github.slimeistdev.crystalline_sky.mixin.client.compat.iris;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.IrisRenderingPipeline_Duck;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@ConditionalMixin(mods = Mods.IRIS)
@Mixin(IrisRenderingPipeline.class)
public class IrisRenderingPipelineMixin implements IrisRenderingPipeline_Duck {
	@Shadow
	public boolean isBeforeTranslucent;

	@Shadow
	private GlFramebuffer defaultFB;

	@Shadow
	private GlFramebuffer defaultFBAlt;

	@Override
	public void crystalline_sky$bindDefaultForRead() {
		if (this.isBeforeTranslucent) {
			this.defaultFB.bindAsReadBuffer();
		} else {
			this.defaultFBAlt.bindAsReadBuffer();
		}
	}
}
