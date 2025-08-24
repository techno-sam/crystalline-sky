package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Set;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
	@ModifyExpressionValue(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/world/ClientWorld;BLOCK_MARKER_ITEMS:Ljava/util/Set;", opcode = Opcodes.PUTSTATIC))
	private static Set<Item> modifyBlockMarkerItems(Set<Item> original) {
		Set<Item> extended = new HashSet<>(original);
		extended.add(CrystallineItems.SKY_LIGHT);
		return Set.copyOf(extended);
	}
}
