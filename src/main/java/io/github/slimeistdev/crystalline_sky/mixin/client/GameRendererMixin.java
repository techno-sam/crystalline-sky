package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineShaderPrograms;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
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
		), (Consumer<ShaderInstance>) program -> CrystallineShaderPrograms.renderTypeSkyProgram = program));
		return original.call(instance, e);
	}
}
