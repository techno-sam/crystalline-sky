package io.github.slimeistdev.crystalline_sky.mixin.client;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
	@Shadow
	@Final
	@Mutable
	private static Set<Item> BLOCK_MARKER_ITEMS;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void modifyBlockMarkerItems(CallbackInfo ci) {
		BLOCK_MARKER_ITEMS = new HashSet<>(BLOCK_MARKER_ITEMS);
		BLOCK_MARKER_ITEMS.add(CrystallineItems.SKY_LIGHT);
		BLOCK_MARKER_ITEMS.add(CrystallineItems.SKY);
		BLOCK_MARKER_ITEMS.add(CrystallineItems.WEEPING_SKY);
	}
}
