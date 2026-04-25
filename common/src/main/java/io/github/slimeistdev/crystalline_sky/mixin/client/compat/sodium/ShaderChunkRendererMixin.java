package io.github.slimeistdev.crystalline_sky.mixin.client.compat.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.compat.sodium.SkyShaderInterface;
import io.github.slimeistdev.crystalline_sky.compat.sodium.SkyboxShaderInterface;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.caffeinemc.mods.sodium.client.gl.shader.GlProgram;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ShaderBindingContext;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@ConditionalMixin(mods = Mods.SODIUM)
@Mixin(ShaderChunkRenderer.class)
public class ShaderChunkRendererMixin {
	@WrapOperation(method = "compileProgram", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/ShaderChunkRenderer;createShader(Ljava/lang/String;Lnet/caffeinemc/mods/sodium/client/render/chunk/shader/ChunkShaderOptions;)Lnet/caffeinemc/mods/sodium/client/gl/shader/GlProgram;"), remap = false)
	private GlProgram<ChunkShaderInterface> createSkyShader(ShaderChunkRenderer instance, String path, ChunkShaderOptions options, Operation<GlProgram<ChunkShaderInterface>> original) {
		if (options.pass() == DefaultMaterials.forRenderLayer(CrystallineRenderTypes.SKY).pass) {
			return original.call(instance, "sodium_compat/block_layer_sky", options);
		} else if (options.pass() == DefaultMaterials.forRenderLayer(CrystallineRenderTypes.SKYBOX).pass) {
			return original.call(instance, "sodium_compat/block_layer_skybox", options);
		}

		return original.call(instance, path, options);
	}

	@WrapOperation(method = "createShader", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
	private ResourceLocation redirectShaderNamespace(String namespace, String path, Operation<ResourceLocation> original, String pathArg) {
		if (pathArg.equals("sodium_compat/block_layer_sky") || pathArg.equals("sodium_compat/block_layer_skybox")) {
			namespace = "crystalline_sky";
		}

		return original.call(namespace, path);
	}

	@WrapOperation(method = "createShader", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/gl/shader/GlProgram$Builder;link(Ljava/util/function/Function;)Lnet/caffeinemc/mods/sodium/client/gl/shader/GlProgram;"), remap = false)
	private static GlProgram<ChunkShaderInterface> useSkyShaderInterface(GlProgram.Builder instance, Function<ShaderBindingContext, ChunkShaderInterface> factory, Operation<GlProgram<ChunkShaderInterface>> original, String argPath, ChunkShaderOptions argOptions) {
		if (argPath.equals("sodium_compat/block_layer_sky")) {
			factory = (shader) -> new SkyShaderInterface(shader, argOptions);
		} else if (argPath.equals("sodium_compat/block_layer_skybox")) {
			factory = (shader) -> new SkyboxShaderInterface(shader, argOptions);
		}

		return original.call(instance, factory);
	}
}
