package io.github.slimeistdev.crystalline_sky.mixin.client.compat.iris;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.mixin.client.compat.sodium.TerrainRenderPassAccessor;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.irisshaders.iris.pipeline.programs.SodiumPrograms;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ConditionalMixin(mods = Mods.IRIS)
@Mixin(SodiumPrograms.class)
public class SodiumProgramsMixin {
	@Inject(method = "mapTerrainRenderPass", at = @At(value = "NEW", target = "(Ljava/lang/String;)Ljava/lang/IllegalArgumentException;"), cancellable = true)
	private static void customRenderTypes(TerrainRenderPass pass, CallbackInfoReturnable<SodiumPrograms.Pass> cir) {
		RenderType renderType = ((TerrainRenderPassAccessor) pass).crystalline_sky$getRenderLayer();
		if (renderType == CrystallineRenderTypes.SKY || renderType == CrystallineRenderTypes.SKYBOX) {
			cir.setReturnValue(ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? SodiumPrograms.Pass.SHADOW : SodiumPrograms.Pass.TERRAIN);
		}
	}
}
