package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.extenders_cove.BlockRenderLayerExt;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@ConditionalMixin(mods = Mods.SODIUM, applyIfPresent = false)
@Mixin(WorldRenderer.class)
public class WorldRenderer_NoSodium_Mixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private int ticks;

	@WrapOperation(method = "renderBlockLayers", at = @At(value = "NEW", target = "net/minecraft/client/gl/DynamicUniforms$UniformValue"))
	private DynamicUniforms.UniformValue tintSkyBlocks(Matrix4fc modelView, Vector4fc colorModulator,
													   Vector3fc modelOffset, Matrix4fc textureMatrix, float lineWidth,
													   Operation<DynamicUniforms.UniformValue> original,
													   @Local BlockRenderLayer blockRenderLayer) {
		if (blockRenderLayer == BlockRenderLayerExt.CRYSTALLINE_SKY_SKY
			&& client.player != null
			&& (client.player.getMainHandStack().isOf(CrystallineItems.SKY)
			|| client.player.getOffHandStack().isOf(CrystallineItems.SKY)
			|| client.player.getMainHandStack().isOf(CrystallineItems.WEEPING_SKY)
			|| client.player.getOffHandStack().isOf(CrystallineItems.WEEPING_SKY))) {

			float f = ticks + client.getRenderTickCounter().getTickProgress(true);
			float alpha = (MathHelper.sin(f / 10.0f) + 1.0f) / 2.0f;
			// remap alpha from [0, 1] to [0, 0.75]
			float maxAlpha = 1.0f - 0.25f;
			alpha = alpha * maxAlpha;
			colorModulator = new Vector4f(1.0f, 1.0f, 1.0f, alpha).mul(colorModulator);
		}

		return original.call(modelView, colorModulator, modelOffset, textureMatrix, lineWidth);
	}
}
