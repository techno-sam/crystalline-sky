package io.github.slimeistdev.crystalline_sky.fabric.mixin.client;

import com.google.common.collect.ImmutableList;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.RenderType_Duck;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderType.class)
public class RenderTypeMixin implements RenderType_Duck {
	@Shadow
	@Final
	@Mutable
	private static ImmutableList<RenderType> CHUNK_BUFFER_LAYERS;

	@Override
	public void crystalline_sky$markAsBlockLayer() {
		CHUNK_BUFFER_LAYERS = ImmutableList.<RenderType>builder()
			.addAll(CHUNK_BUFFER_LAYERS)
			.add((RenderType) (Object) this)
			.build();
	}

	// must do this _now_, otherwise it won't inject in time for other rendering classes to get the right block layers
	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void loadCustom(CallbackInfo ci) {
		CrystallineRenderTypes.init();
	}
}
