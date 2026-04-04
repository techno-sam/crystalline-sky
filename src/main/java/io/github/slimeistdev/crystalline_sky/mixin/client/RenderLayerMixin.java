package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderLayers;
import net.minecraft.client.render.RenderLayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderLayer.class)
public class RenderLayerMixin {
	@WrapOperation(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderLayer;BLOCK_LAYERS:Lcom/google/common/collect/ImmutableList;", opcode = Opcodes.PUTSTATIC))
	private static void addSkyToBlockLayers(ImmutableList<RenderLayer> value, Operation<Void> original) {
		original.call(ImmutableList.<RenderLayer>builder()
			.addAll(value)
			.add(CrystallineRenderLayers.SKY)
			.build());
	}
}
