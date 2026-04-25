package io.github.slimeistdev.crystalline_sky.fabric.mixin.client.compat.axiom;

import com.moulberry.axiom.render.ChunkRenderOverrider;
import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ConditionalMixin(mods = Mods.AXIOM)
@Mixin(ChunkRenderOverrider.AxiomChunkOverrideLayer.class)
public class ChunkRenderOverrider_AxiomChunkOverrideLayerMixin {
	@Inject(method = "fromVanilla", at = @At("HEAD"), cancellable = true)
	private static void unexplodeSky(RenderType layer, CallbackInfoReturnable<ChunkRenderOverrider.AxiomChunkOverrideLayer> cir) {
		if (layer == CrystallineRenderTypes.SKY || layer == CrystallineRenderTypes.SKYBOX) {
			cir.setReturnValue(ChunkRenderOverrider.AxiomChunkOverrideLayer.CUTOUT_MIPPED);
		}
	}
}
