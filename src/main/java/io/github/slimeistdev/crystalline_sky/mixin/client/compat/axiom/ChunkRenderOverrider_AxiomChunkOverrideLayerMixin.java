package io.github.slimeistdev.crystalline_sky.mixin.client.compat.axiom;

import com.moulberry.axiom.render.ChunkRenderOverrider;
import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.extenders_cove.BlockRenderLayerExt;
import net.minecraft.client.render.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ConditionalMixin(mods = Mods.AXIOM)
@Mixin(ChunkRenderOverrider.AxiomChunkOverrideLayer.class)
public class ChunkRenderOverrider_AxiomChunkOverrideLayerMixin {
	@Inject(method = "fromVanilla", at = @At(value = "NEW", target = "()Ljava/lang/IncompatibleClassChangeError;"), cancellable = true)
	private static void unexplodeSky(BlockRenderLayer layer, CallbackInfoReturnable<ChunkRenderOverrider.AxiomChunkOverrideLayer> cir) {
		if (layer == BlockRenderLayerExt.CRYSTALLINE_SKY_SKY) {
			cir.setReturnValue(ChunkRenderOverrider.AxiomChunkOverrideLayer.CUTOUT_MIPPED);
		}
	}
}
