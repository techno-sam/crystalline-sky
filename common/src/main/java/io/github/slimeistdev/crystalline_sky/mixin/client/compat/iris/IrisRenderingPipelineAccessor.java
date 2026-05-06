package io.github.slimeistdev.crystalline_sky.mixin.client.compat.iris;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.targets.RenderTargets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ConditionalMixin(mods = Mods.IRIS)
@Mixin(IrisRenderingPipeline.class)
public interface IrisRenderingPipelineAccessor {
	@Accessor("packDirectives")
	PackDirectives crystalline_sky$getPackDirectives();

	@Accessor("renderTargets")
	RenderTargets crystalline_sky$getRenderTargets();
}
