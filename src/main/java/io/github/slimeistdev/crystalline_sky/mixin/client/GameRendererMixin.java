package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineShaderPrograms;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@WrapOperation(method = "loadPrograms", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
	private <E extends Pair<ShaderProgram, Consumer<ShaderProgram>>> boolean loadSkyProgram(
		List<E> instance,
		Object e,
		Operation<Boolean> original,
		ResourceFactory factory
	) throws IOException {
		original.call(instance, Pair.of(new ShaderProgram(
			factory,
			"crystalline_sky_rendertype_sky",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL
		), (Consumer<ShaderProgram>) program -> CrystallineShaderPrograms.renderTypeSkyProgram = program));
		return original.call(instance, e);
	}
}
