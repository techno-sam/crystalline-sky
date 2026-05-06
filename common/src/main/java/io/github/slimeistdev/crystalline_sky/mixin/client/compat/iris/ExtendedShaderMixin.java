package io.github.slimeistdev.crystalline_sky.mixin.client.compat.iris;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.util.SharedRenderVariables;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ConditionalMixin(mods = Mods.IRIS)
@Mixin(ExtendedShader.class)
public class ExtendedShaderMixin {
	@Inject(method = "bindFramebuffer", at = @At("HEAD"), cancellable = true)
	private void skipBind(CallbackInfo ci) {
		if (SharedRenderVariables.shouldBlockIrisShaderFramebufferBind())
			ci.cancel();
	}
}
