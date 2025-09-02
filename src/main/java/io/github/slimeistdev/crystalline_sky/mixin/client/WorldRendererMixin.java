package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import io.github.slimeistdev.crystalline_sky.extenders_cove.BlockRenderLayerGroupExt;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DefaultFramebufferSet_Duck;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.Framebuffer_Duck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
	@Shadow
	@Final
	private DefaultFramebufferSet framebufferSet;

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private @Nullable ClientWorld world;

	@Shadow
	private int ticks;

	@Shadow
	@Final
	private CloudRenderer cloudRenderer;

	@Shadow
	protected abstract boolean hasBlindnessOrDarkness(Camera camera);

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;getTransparencyPostEffectProcessor()Lnet/minecraft/client/gl/PostEffectProcessor;"))
	private void setupSkyBuffer(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline,
								Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog,
								Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci,
								@Local FrameGraphBuilder frameGraphBuilder,
								@Local SimpleFramebufferFactory factory) {
		Handle<Framebuffer> framebuffer = frameGraphBuilder.createResourceHandle("crystalline_sky:sky_framebuffer", factory);
		((DefaultFramebufferSet_Duck) framebufferSet).crystalline_sky$setSkyFramebuffer(framebuffer);
	}

	@Unique
	private Handle<Framebuffer> crystalline_sky$transferSkyBuffer(FramePass pass) {
		Handle<Framebuffer> skyFramebuffer = pass.transfer(
			((DefaultFramebufferSet_Duck) framebufferSet).crystalline_sky$getSkyFramebuffer());
		((DefaultFramebufferSet_Duck) framebufferSet).crystalline_sky$setSkyFramebuffer(skyFramebuffer);
		return skyFramebuffer;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderMain(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;ZZLnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/util/profiler/Profiler;)V"))
	private void copyAfterSky(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline,
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
	}

	@Inject(method = "renderMain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/FramePass;transfer(Lnet/minecraft/client/util/Handle;)Lnet/minecraft/client/util/Handle;", ordinal = 0))
	private void transferCrystallineSky(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Camera camera,
										Matrix4f positionMatrix, GpuBufferSlice fog, boolean renderBlockOutline,
										boolean renderEntityOutline, RenderTickCounter tickCounter, Profiler profiler,
										CallbackInfo ci, @Local FramePass framePass) {
		crystalline_sky$transferSkyBuffer(framePass);
	}

	// renderMain : framePass.setRenderer(@{() -> ...});
	@Inject(
		method = "method_62214",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/SectionRenderState;renderSection(Lnet/minecraft/client/render/BlockRenderLayerGroup;)V",
			ordinal = 0,
			shift = At.Shift.AFTER
		)
	)
	private void renderCrystallineSky(GpuBufferSlice fog, RenderTickCounter tickCounter, Camera camera,
									  Profiler profiler, Matrix4f positionMatrix, Handle<Framebuffer> itemEntityFramebuffer,
									  Handle<Framebuffer> entityOutlineFramebuffer, boolean renderBlockOutline, Frustum frustum,
									  Handle<Framebuffer> translucentFramebuffer, Handle<Framebuffer> mainFramebuffer, CallbackInfo ci,
									  @Local SectionRenderState sectionRenderState) {
		sectionRenderState.renderSection(BlockRenderLayerGroupExt.CRYSTALLINE_SKY_SKY);
	}
}
