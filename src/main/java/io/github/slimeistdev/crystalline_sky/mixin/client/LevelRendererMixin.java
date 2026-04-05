package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.LevelRenderer_Duck;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.RenderTarget_Duck;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, remap = false)
public abstract class LevelRendererMixin implements LevelRenderer_Duck {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	protected abstract boolean doesMobEffectBlockSky(Camera camera);

	@Shadow
	protected abstract void renderSectionLayer(RenderType renderLayer, double x, double y, double z, Matrix4f matrix4f, Matrix4f positionMatrix);

	@Shadow
	private @Nullable RenderTarget cloudsTarget;

	@Shadow
	public abstract void renderClouds(PoseStack matrices, Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, double cameraX, double cameraY, double cameraZ);

	@Unique
	@Nullable
	private RenderTarget crystalline_sky$skyBuffer;

	@Override
	public RenderTarget crystalline_sky$getSkyFramebuffer() {
		if (crystalline_sky$skyBuffer == null) {
			var fb = minecraft.getMainRenderTarget();
			crystalline_sky$skyBuffer = new TextureTarget(fb.width, fb.height, true, Minecraft.ON_OSX);
		}
		return crystalline_sky$skyBuffer;
	}

	@Inject(
		method = "renderLevel",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=clear",
			shift = At.Shift.AFTER
		)
	)
	private void setupSkyBuffer(DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera,
								GameRenderer gameRenderer, LightTexture lightmapTextureManager,
								Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
		crystalline_sky$getSkyFramebuffer();
	}

	@Inject(method = "resize", at = @At("RETURN"))
	private void resizeSkyBuffer(int width, int height, CallbackInfo ci) {
		if (crystalline_sky$skyBuffer != null) {
			crystalline_sky$skyBuffer.resize(width, height, Minecraft.ON_OSX);
		}
	}

	@Inject(method = "close", at = @At("RETURN"))
	private void closeSkyBuffer(CallbackInfo ci) {
		if (crystalline_sky$skyBuffer != null) {
			crystalline_sky$skyBuffer.destroyBuffers();
			crystalline_sky$skyBuffer = null;
		}
	}

	@Inject(
		method = "renderLevel",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=terrain_setup"
		)
	)
	private void copyAfterSky(DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera,
							  GameRenderer gameRenderer, LightTexture lightmapTextureManager,
							  Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
		var skyBuffer = crystalline_sky$getSkyFramebuffer();
		skyBuffer.clear(Minecraft.ON_OSX);
		((RenderTarget_Duck) skyBuffer).crystalline_sky$copyColorFrom(minecraft.getMainRenderTarget());

		// render clouds
		CloudStatus cloudRenderMode = this.minecraft.options.getCloudsType();
		if (cloudRenderMode != CloudStatus.OFF && !this.doesMobEffectBlockSky(camera)) {
			float f = tickCounter.getGameTimeDeltaPartialTick(false);
			Vec3 vec3d = camera.getPosition();
			double cx = vec3d.x();
			double cy = vec3d.y();
			double cz = vec3d.z();

			var cloudsFramebuffer0 = this.cloudsTarget;
			this.cloudsTarget = skyBuffer;
			cloudsTarget.bindWrite(false);
			this.renderClouds(new PoseStack(), matrix4f, matrix4f2, f, cx, cy, cz);
			minecraft.getMainRenderTarget().bindWrite(false);
			this.cloudsTarget = cloudsFramebuffer0;
		}
	}

	// after solid, cutoutMipped, and cutout
	@WrapOperation(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
			ordinal = 2
		)
	)
	private void renderCrystallineSky(LevelRenderer instance, RenderType renderLayer, double x, double y, double z,
									  Matrix4f matrix4f, Matrix4f positionMatrix, Operation<Void> original) {
		original.call(instance, renderLayer, x, y, z, matrix4f, positionMatrix);
		renderSectionLayer(CrystallineRenderTypes.SKY, x, y, z, matrix4f, positionMatrix);
	}
}
