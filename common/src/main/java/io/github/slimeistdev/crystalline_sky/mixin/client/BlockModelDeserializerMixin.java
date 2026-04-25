package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin {
	@WrapOperation(method = "getTextureMap", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;LOCATION_BLOCKS:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC))
	private ResourceLocation modifyAtlas(Operation<ResourceLocation> original, JsonObject json) {
		if (json.has("crystalline_sky:atlas")) {
			return ResourceLocation.parse(json.get("crystalline_sky:atlas").getAsString());
		} else {
			return original.call();
		}
	}
}
