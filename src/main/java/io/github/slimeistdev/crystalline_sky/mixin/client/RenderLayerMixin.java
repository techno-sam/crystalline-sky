package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.google.common.collect.ImmutableList;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.RenderLayer_Duck;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderLayer.class)
public class RenderLayerMixin implements RenderLayer_Duck {
	@Shadow
	@Final
	@Mutable
	private static ImmutableList<RenderLayer> BLOCK_LAYERS;

	@Override
	public void crystalline_sky$markAsBlockLayer() {
		BLOCK_LAYERS = ImmutableList.<RenderLayer>builder()
			.addAll(BLOCK_LAYERS)
			.add((RenderLayer) (Object) this)
			.build();
	}
}
