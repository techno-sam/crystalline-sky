package io.github.slimeistdev.crystalline_sky.mixin.client.compat.sodium;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ConditionalMixin(mods = Mods.SODIUM)
@Mixin(TerrainRenderPass.class)
public interface TerrainRenderPassAccessor {
	@Accessor("renderType")
	RenderType crystalline_sky$getRenderLayer();
}
