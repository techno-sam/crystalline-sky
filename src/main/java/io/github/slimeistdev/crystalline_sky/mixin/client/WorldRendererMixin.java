package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.Framebuffer_Duck;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.WorldRenderer_Duck;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class, remap = false)
public abstract class WorldRendererMixin implements WorldRenderer_Duck {
	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	protected abstract boolean hasBlindnessOrDarkness(Camera camera);

	@Shadow
	protected abstract void renderLayer(RenderLayer renderLayer, double x, double y, double z, Matrix4f matrix4f, Matrix4f positionMatrix);

	@Shadow
	private @Nullable Framebuffer cloudsFramebuffer;

	@Shadow
	public abstract void renderClouds(MatrixStack matrices, Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, double cameraX, double cameraY, double cameraZ);

	@Unique
	@Nullable
	private Framebuffer crystalline_sky$skyBuffer;

	@Override
	public Framebuffer crystalline_sky$getSkyFramebuffer() {
		if (crystalline_sky$skyBuffer == null) {
			var fb = client.getFramebuffer();
			crystalline_sky$skyBuffer = new SimpleFramebuffer(fb.textureWidth, fb.textureHeight, true, MinecraftClient.IS_SYSTEM_MAC);
		}
		return crystalline_sky$skyBuffer;
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=clear",
			shift = At.Shift.AFTER
		)
	)
	private void setupSkyBuffer(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
								GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
								Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
		crystalline_sky$getSkyFramebuffer();
	}

	@Inject(method = "onResized", at = @At("RETURN"))
	private void resizeSkyBuffer(int width, int height, CallbackInfo ci) {
		if (crystalline_sky$skyBuffer != null) {
			crystalline_sky$skyBuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
		}
	}

	@Inject(method = "close", at = @At("RETURN"))
	private void closeSkyBuffer(CallbackInfo ci) {
		if (crystalline_sky$skyBuffer != null) {
			crystalline_sky$skyBuffer.delete();
			crystalline_sky$skyBuffer = null;
		}
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=terrain_setup"
		)
	)
	private void copyAfterSky(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
							  GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
							  Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
		var skyBuffer = crystalline_sky$getSkyFramebuffer();
		skyBuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
		((Framebuffer_Duck) skyBuffer).crystalline_sky$copyColorFrom(client.getFramebuffer());

		// render clouds
		CloudRenderMode cloudRenderMode = this.client.options.getCloudRenderModeValue();
		if (cloudRenderMode != CloudRenderMode.OFF && !this.hasBlindnessOrDarkness(camera)) {
			float f = tickCounter.getTickDelta(false);
			Vec3d vec3d = camera.getPos();
			double cx = vec3d.getX();
			double cy = vec3d.getY();
			double cz = vec3d.getZ();

			var cloudsFramebuffer0 = this.cloudsFramebuffer;
			this.cloudsFramebuffer = skyBuffer;
			cloudsFramebuffer.beginWrite(false);
			this.renderClouds(new MatrixStack(), matrix4f, matrix4f2, f, cx, cy, cz);
			client.getFramebuffer().beginWrite(false);
			this.cloudsFramebuffer = cloudsFramebuffer0;
		}
	}

	// after solid, cutoutMipped, and cutout
	@WrapOperation(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
			ordinal = 2
		)
	)
	private void renderCrystallineSky(WorldRenderer instance, RenderLayer renderLayer, double x, double y, double z,
									  Matrix4f matrix4f, Matrix4f positionMatrix, Operation<Void> original) {
		original.call(instance, renderLayer, x, y, z, matrix4f, positionMatrix);
		renderLayer(CrystallineRenderLayers.SKY, x, y, z, matrix4f, positionMatrix);
	}
}
