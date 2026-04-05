package io.github.slimeistdev.crystalline_sky.mixin.client;

import io.github.slimeistdev.crystalline_sky.infrastructure.client.WeepingSkyDebugRenderer;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DebugRenderer_Duck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin implements DebugRenderer_Duck {
	@Unique
	@Mutable
	@Final
	private WeepingSkyDebugRenderer crystalline_sky$weepingSkyDebugRenderer;

	@Unique
	private int crystalline_sky$weepingSkyMode = 0;

	@Override
	public WeepingSkyDebugRenderer crystalline_sky$getWeepingSkyDebugRenderer() {
		return crystalline_sky$weepingSkyDebugRenderer;
	}

	@Override
	public int crystalline_sky$toggleWeepingSky() {
		crystalline_sky$weepingSkyMode = (crystalline_sky$weepingSkyMode + 1) % 3;
		return crystalline_sky$weepingSkyMode;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(Minecraft client, CallbackInfo ci) {
		this.crystalline_sky$weepingSkyDebugRenderer = new WeepingSkyDebugRenderer(client);
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void render(PoseStack matrices, MultiBufferSource.BufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if (crystalline_sky$weepingSkyMode != 0) {
			crystalline_sky$weepingSkyDebugRenderer.setLitMode(crystalline_sky$weepingSkyMode == 1);
			crystalline_sky$weepingSkyDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
		}
	}

	@Inject(method = "clear", at = @At("RETURN"))
	private void reset(CallbackInfo ci) {
		crystalline_sky$weepingSkyDebugRenderer.clear();
	}
}
