package io.github.slimeistdev.crystalline_sky.mixin.client.compat.sodium;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.extenders_cove.BlockRenderLayerExt;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import net.minecraft.client.render.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@ConditionalMixin(mods = Mods.SODIUM)
@Mixin(DefaultMaterials.class)
public class DefaultMaterialsMixin {
	@Unique
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private static final Material crystalline_sky$SKY = new Material(
		Arrays.stream(DefaultTerrainRenderPasses.ALL)
			.filter(pass -> ((TerrainRenderPassAccessor) pass).crystalline_sky$getRenderLayer() == BlockRenderLayerExt.CRYSTALLINE_SKY_SKY)
			.findFirst()
			.get(),
		AlphaCutoffParameter.ZERO,
		false
	);

	@Inject(method = "forChunkLayer", at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/Throwable;)Ljava/lang/MatchException;"), cancellable = true)
	private static void addCrystallineSkyMaterial(BlockRenderLayer layer, CallbackInfoReturnable<Material> cir) {
		if (layer == BlockRenderLayerExt.CRYSTALLINE_SKY_SKY) {
			cir.setReturnValue(crystalline_sky$SKY);
		}
	}
}
