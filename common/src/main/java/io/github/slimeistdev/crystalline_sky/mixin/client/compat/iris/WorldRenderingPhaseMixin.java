package io.github.slimeistdev.crystalline_sky.mixin.client.compat.iris;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ConditionalMixin(mods = Mods.IRIS)
@Mixin(WorldRenderingPhase.class)
public class WorldRenderingPhaseMixin {
	@Inject(method = "fromTerrainRenderType", at = @At(value = "NEW", target = "(Ljava/lang/String;)Ljava/lang/IllegalStateException;"), cancellable = true)
	private static void customRenderTypes(RenderType renderType, CallbackInfoReturnable<WorldRenderingPhase> cir) {
		if (renderType == CrystallineRenderTypes.SKY || renderType == CrystallineRenderTypes.SKYBOX)
			cir.setReturnValue(WorldRenderingPhase.TERRAIN_SOLID);
	}
}
