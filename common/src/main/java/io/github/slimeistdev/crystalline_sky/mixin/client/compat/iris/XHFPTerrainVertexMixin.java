package io.github.slimeistdev.crystalline_sky.mixin.client.compat.iris;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import net.irisshaders.iris.vertices.sodium.terrain.XHFPTerrainVertex;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ConditionalMixin(mods = Mods.IRIS)
@Mixin(XHFPTerrainVertex.class)
public class XHFPTerrainVertexMixin {
	@Definition(id = "quantized", local = @Local(type = int.class, name = "quantized"))
	@Expression("quantized = @(?)")
	@ModifyExpressionValue(method = "encodeTexture", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int clamp(int original) {
		return Mth.clamp(original, 0, 32767);
	}
}
