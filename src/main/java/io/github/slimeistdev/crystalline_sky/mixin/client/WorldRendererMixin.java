package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.Framebuffer_Duck;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.WorldRenderer_Duck;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements WorldRenderer_Duck {
	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private @Nullable ClientWorld world;

	@Shadow
	private int ticks;

	@Shadow
	protected abstract boolean hasBlindnessOrDarkness(Camera camera);

	@Shadow
	protected abstract void renderLayer(RenderLayer renderLayer, double x, double y, double z, Matrix4f matrix4f, Matrix4f positionMatrix);

	@Unique
	@Nullable
	private Framebuffer crystalline_sky$skyBuffer;// = new SimpleFramebuffer(0, 0, false, MinecraftClient.IS_SYSTEM_MAC);

	@Override
	public Framebuffer crystalline_sky$getSkyFramebuffer() {
		if (crystalline_sky$skyBuffer == null) {
			var fb = client.getFramebuffer();
			crystalline_sky$skyBuffer = new SimpleFramebuffer(fb.textureWidth, fb.textureHeight, false, MinecraftClient.IS_SYSTEM_MAC);
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
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/WorldRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
			shift = At.Shift.AFTER
		)
	)
	private void copyAfterSky(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
							  GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
							  Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
		var skyBuffer = crystalline_sky$getSkyFramebuffer();
		((Framebuffer_Duck) skyBuffer).crystalline_sky$copyColorFrom(client.getFramebuffer());
		// TODO: render clouds
	}

/*	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderMain(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;ZZLnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/util/profiler/Profiler;)V"))
	private void copyAfterSky2(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline,
							  Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog,
							  Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci,
							  @Local FrameGraphBuilder frameGraphBuilder) {
		FramePass skyCopyPass = frameGraphBuilder.createPass("crystalline_sky:copy_sky_pass");

		framebufferSet.mainFramebuffer = skyCopyPass.transfer(framebufferSet.mainFramebuffer);

		Handle<Framebuffer> skyFramebuffer = crystalline_sky$transferSkyBuffer(skyCopyPass);

		skyCopyPass.setRenderer(() -> {
			((Framebuffer_Duck) skyFramebuffer.get()).crystalline_sky$copyColorFrom(framebufferSet.mainFramebuffer.get());
		});

		// render clouds
		float f = tickCounter.getTickProgress(false);

		CloudRenderMode cloudRenderMode = this.client.options.getCloudRenderModeValue();
		if (cloudRenderMode != CloudRenderMode.OFF && !this.hasBlindnessOrDarkness(camera)) {
			//noinspection DataFlowIssue
			Optional<Integer> optional = this.world.getDimension().cloudHeight();
			if (optional.isPresent()) {
				float cloudPhase = this.ticks + f;
				int color = this.world.getCloudsColor(f);

				// modified renderClouds function
				{
					FramePass framePass = frameGraphBuilder.createPass("crystallized clouds");
					crystalline_sky$transferSkyBuffer(framePass);

					framePass.setRenderer(() -> {
						Handle<Framebuffer> backupCloudBuffer = this.framebufferSet.cloudsFramebuffer;
						this.framebufferSet.cloudsFramebuffer = ((DefaultFramebufferSet_Duck) framebufferSet).crystalline_sky$getSkyFramebuffer();

						this.cloudRenderer.renderClouds(color, cloudRenderMode, optional.get() + 0.33F, camera.getPos(), cloudPhase);

						((DefaultFramebufferSet_Duck) framebufferSet).crystalline_sky$setSkyFramebuffer(framebufferSet.cloudsFramebuffer);
						this.framebufferSet.cloudsFramebuffer = backupCloudBuffer;
					});
				}
			}
		}
	}*/

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
