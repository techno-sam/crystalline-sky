package io.github.slimeistdev.crystalline_sky.mixin.client.compat.sodium;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderLayers;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import net.minecraft.client.renderer.RenderType;
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
			.filter(pass -> ((TerrainRenderPassAccessor) pass).crystalline_sky$getRenderLayer() == CrystallineRenderLayers.SKY)
			.findFirst()
			.get(),
		AlphaCutoffParameter.ZERO,
		false
	);

	@Inject(method = "forRenderLayer", at = @At(value = "NEW", target = "(Ljava/lang/String;)Ljava/lang/IllegalArgumentException;"), cancellable = true)
	private static void addCrystallineSkyMaterial(RenderType layer, CallbackInfoReturnable<Material> cir) {
		if (layer == CrystallineRenderLayers.SKY) {
			cir.setReturnValue(crystalline_sky$SKY);
		}
	}
}
