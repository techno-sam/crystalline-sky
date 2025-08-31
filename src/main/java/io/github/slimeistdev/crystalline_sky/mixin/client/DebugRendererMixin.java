package io.github.slimeistdev.crystalline_sky.mixin.client;

import io.github.slimeistdev.crystalline_sky.infrastructure.client.WeepingSkyDebugRenderer;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DebugRenderer_Duck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
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
	private void init(MinecraftClient client, CallbackInfo ci) {
		this.crystalline_sky$weepingSkyDebugRenderer = new WeepingSkyDebugRenderer(client);
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void render(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		if (crystalline_sky$weepingSkyMode != 0) {
			crystalline_sky$weepingSkyDebugRenderer.setLitMode(crystalline_sky$weepingSkyMode == 1);
			crystalline_sky$weepingSkyDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
		}
	}

	@Inject(method = "reset", at = @At("RETURN"))
	private void reset(CallbackInfo ci) {
		crystalline_sky$weepingSkyDebugRenderer.clear();
	}
}
