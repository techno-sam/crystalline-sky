package io.github.slimeistdev.crystalline_sky.mixin.client.compat.sodium;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderLayers;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ConditionalMixin(mods = Mods.SODIUM)
@Mixin(DefaultTerrainRenderPasses.class)
public class DefaultTerrainRenderPassesMixin {
	@Mutable
	@Shadow
	@Final
	public static TerrainRenderPass[] ALL;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void addCrystallineSkyPass(CallbackInfo ci) {
		var ALL$ = new TerrainRenderPass[ALL.length + 1];
		System.arraycopy(ALL, 0, ALL$, 0, ALL.length);
		ALL$[ALL.length] = new TerrainRenderPass(CrystallineRenderLayers.SKY, false, true);
		ALL = ALL$;
	}
}
