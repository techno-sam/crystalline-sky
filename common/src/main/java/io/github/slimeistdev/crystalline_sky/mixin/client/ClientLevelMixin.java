package io.github.slimeistdev.crystalline_sky.mixin.client;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import io.github.slimeistdev.crystalline_sky.util.SharedRenderVariables;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
	@Shadow
	@Final
	@Mutable
	private static Set<Item> MARKER_PARTICLE_ITEMS;

	@Unique
	private static final Float crystalline_sky$fullBright = 1.0f;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void modifyBlockMarkerItems(CallbackInfo ci) {
		MARKER_PARTICLE_ITEMS = new HashSet<>(MARKER_PARTICLE_ITEMS);
		MARKER_PARTICLE_ITEMS.add(CrystallineItems.SKY_LIGHT.value());
		MARKER_PARTICLE_ITEMS.add(CrystallineItems.WEEPING_SKY_LIGHT.value());
		MARKER_PARTICLE_ITEMS.add(CrystallineItems.SKY.value());
		MARKER_PARTICLE_ITEMS.add(CrystallineItems.WEEPING_SKY.value());
	}

	@Inject(method = "getShade", at = @At("HEAD"), cancellable = true)
	private void fullBrightSkyBox(Direction direction, boolean shade, CallbackInfoReturnable<Float> cir) {
		if (SharedRenderVariables.shouldShadeFullBright())
			cir.setReturnValue(crystalline_sky$fullBright);
	}
}
