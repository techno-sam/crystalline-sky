package io.github.slimeistdev.crystalline_sky.neoforge.mixin.client;

import com.google.common.collect.ImmutableList;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.RenderType_Duck;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderType.class)
public class RenderTypeMixin implements RenderType_Duck {
	@Shadow
	@Final
	@Mutable
	public static ImmutableList<RenderType> CHUNK_BUFFER_LAYERS;

	@Shadow
	public int chunkLayerId;

	@Override
	public void crystalline_sky$markAsBlockLayer() {
		CHUNK_BUFFER_LAYERS = ImmutableList.<RenderType>builder()
			.addAll(CHUNK_BUFFER_LAYERS)
			.add((RenderType) (Object) this)
			.build();
		chunkLayerId = CHUNK_BUFFER_LAYERS.size() - 1;
	}
}
