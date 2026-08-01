package io.github.slimeistdev.crystalline_sky.neoforge.mixin.client.compat.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.InputStream;

@ConditionalMixin(mods = Mods.SODIUM)
@Mixin(ShaderLoader.class)
public class ShaderLoaderMixin {
	// thank you neoforge for making mods be in separate modules
	@WrapOperation(method = "getShaderSource", at = @At(value = "INVOKE", target = "Ljava/lang/ClassLoader;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"))
	private static InputStream getShadersInProperContext(ClassLoader instance, String name, Operation<InputStream> original, @Local(argsOnly = true) ResourceLocation name$) {
		if (name$.getNamespace().equals(CrystallineSky.ID))
			return original.call(CrystallineSky.class.getClassLoader(), name);

		return original.call(instance, name);
	}
}
