package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineAtlases;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
	// lucky us: fabric calls it bakedQuad, neoforge bakedquad. But naming doesn't work in prod fabric anyway :(
	@SuppressWarnings("LocalMayUseName")
	@WrapOperation(method = "renderModelFaceFlat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockAndTintGetter;getShade(Lnet/minecraft/core/Direction;Z)F"))
	private float fullBrightSkybox(BlockAndTintGetter instance, Direction direction, boolean shade,
								   Operation<Float> original, @Local/*(name = {"bakedQuad", "bakedquad"})*/ BakedQuad bakedQuad) {
		if (bakedQuad.getSprite().atlasLocation().equals(CrystallineAtlases.SKYBOXES.texture))
			return 1.0f;
		return original.call(instance, direction, shade);
	}
}
