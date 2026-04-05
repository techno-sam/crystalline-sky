package io.github.slimeistdev.crystalline_sky.mixin.client.compat.sodium;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ConditionalMixin(mods = Mods.SODIUM)
@Mixin(SodiumWorldRenderer.class)
public class SodiumWorldRendererMixin {
	@Shadow
	private RenderSectionManager renderSectionManager;

	@Inject(method = "drawChunkLayer", at = @At("RETURN"))
	private void drawCrystallineLayer(RenderType renderLayer, ChunkRenderMatrices matrices, double x, double y, double z, CallbackInfo ci) {
		if (renderLayer != CrystallineRenderTypes.SKY) return;

		renderSectionManager.renderLayer(matrices, DefaultMaterials.forRenderLayer(CrystallineRenderTypes.SKY).pass, x, y, z);
	}
}
