package io.github.slimeistdev.crystalline_sky.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin {
	@SuppressWarnings("LocalMayUseName")
	@WrapOperation(method = "getTextureMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModel$Deserializer;parseTextureLocationOrReference(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;)Lcom/mojang/datafixers/util/Either;"))
	private Either<Material, String> modifyAtlas(ResourceLocation location, String name,
												 Operation<Either<Material, String>> original, JsonObject json,
												 @Local Map.Entry<String, JsonElement> entry) {
		if (!entry.getKey().equals("particle") && json.has("crystalline_sky:atlas")) {
			location = ResourceLocation.parse(json.get("crystalline_sky:atlas").getAsString());
		}

		return original.call(location, name);
	}
}
