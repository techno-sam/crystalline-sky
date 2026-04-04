package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
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

	@WrapOperation(method = "renderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/ShaderProgram;initializeUniforms(Lnet/minecraft/client/render/VertexFormat$DrawMode;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/util/Window;)V"))
	private void tintSkyBlocks(ShaderProgram instance, VertexFormat.DrawMode drawMode, Matrix4f viewMatrix,
							   Matrix4f projectionMatrix, Window window, Operation<Void> original,
							   RenderLayer renderLayer) {
		original.call(instance, drawMode, viewMatrix, projectionMatrix, window);

		if (renderLayer == CrystallineRenderLayers.SKY
			&& instance.colorModulator != null
			&& client.player != null
			&& (client.player.getMainHandStack().isOf(CrystallineItems.SKY)
			|| client.player.getOffHandStack().isOf(CrystallineItems.SKY)
			|| client.player.getMainHandStack().isOf(CrystallineItems.WEEPING_SKY)
			|| client.player.getOffHandStack().isOf(CrystallineItems.WEEPING_SKY))) {

			float f = ticks + client.getRenderTickCounter().getTickDelta(true);
			float alpha = (MathHelper.sin(f / 10.0f) + 1.0f) / 2.0f;
			// remap alpha from [0, 1] to [0, 0.75]
			float maxAlpha = 1.0f - 0.25f;
			alpha = alpha * maxAlpha;

			float[] colorModulator = RenderSystem.getShaderColor().clone();
			colorModulator[3] *= alpha;
			instance.colorModulator.set(colorModulator);
		}
	}
}
