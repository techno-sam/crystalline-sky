package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineShaderInstances;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static io.github.slimeistdev.crystalline_sky.util.SharedRenderVariables.invSkyboxMat;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Unique
	private static final Matrix4f crystalline_sky$scratch = new Matrix4f();

	@WrapOperation(method = "reloadShaders", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
	private <E extends Pair<ShaderInstance, Consumer<ShaderInstance>>> boolean loadSkyProgram(
		List<E> instance,
		Object e,
		Operation<Boolean> original,
		ResourceProvider factory
	) throws IOException {
		original.call(instance, Pair.of(new ShaderInstance(
			factory,
			"crystalline_sky_rendertype_sky",
			DefaultVertexFormat.BLOCK
		), (Consumer<ShaderInstance>) program -> CrystallineShaderInstances.renderTypeSkyProgram = program));
		original.call(instance, Pair.of(new ShaderInstance(
			factory,
			"crystalline_sky_rendertype_skybox",
			DefaultVertexFormat.BLOCK
		), (Consumer<ShaderInstance>) program -> CrystallineShaderInstances.renderTypeSkyboxProgram = program));
		return original.call(instance, e);
	}

	@WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"))
	private void mulInvViewMat(LevelRenderer instance, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, Operation<Void> original) {
		// invSkyboxMat = inv(ModelViewMat) * inv(ProjMat)
		projectionMatrix.invert(invSkyboxMat);
		Matrix4f invViewMat = frustumMatrix.invert(crystalline_sky$scratch);
		invViewMat.mul(invSkyboxMat, invSkyboxMat);
		original.call(instance, deltaTracker, renderBlockOutline, camera, gameRenderer, lightTexture, frustumMatrix, projectionMatrix);
	}
}
