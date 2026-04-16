package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@ConditionalMixin(mods = Mods.SODIUM, applyIfPresent = false)
@Mixin(LevelRenderer.class)
public class LevelRenderer_NoSodium_Mixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	private int ticks;

	@WrapOperation(method = "renderSectionLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ShaderInstance;setDefaultUniforms(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/platform/Window;)V"))
	private void tintSkyBlocks(ShaderInstance instance, VertexFormat.Mode drawMode, Matrix4f viewMatrix,
							   Matrix4f projectionMatrix, Window window, Operation<Void> original,
							   RenderType renderLayer) {
		original.call(instance, drawMode, viewMatrix, projectionMatrix, window);

		if (renderLayer == CrystallineRenderTypes.SKY
			&& instance.COLOR_MODULATOR != null
			&& minecraft.player != null
			&& (CrystallineItems.isSky(minecraft.player.getMainHandItem())
			|| CrystallineItems.isSky(minecraft.player.getOffhandItem()))) {

			float f = ticks + minecraft.getTimer().getGameTimeDeltaPartialTick(true);
			float alpha = (Mth.sin(f / 10.0f) + 1.0f) / 2.0f;
			// remap alpha from [0, 1] to [0, 0.75]
			float maxAlpha = 1.0f - 0.25f;
			alpha = alpha * maxAlpha;

			float[] colorModulator = RenderSystem.getShaderColor().clone();
			colorModulator[3] *= alpha;
			instance.COLOR_MODULATOR.set(colorModulator);
		}
	}
}
