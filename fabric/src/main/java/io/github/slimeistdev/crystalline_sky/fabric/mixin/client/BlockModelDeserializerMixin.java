package io.github.slimeistdev.crystalline_sky.fabric.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import io.github.slimeistdev.crystalline_sky.fabric.mixin_ducks.client.BlockModel_Duck;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineAtlases;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;

@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin {
	@WrapOperation(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceLocation;Ljava/util/List;Ljava/util/Map;Ljava/lang/Boolean;Lnet/minecraft/client/renderer/block/model/BlockModel$GuiLight;Lnet/minecraft/client/renderer/block/model/ItemTransforms;Ljava/util/List;)Lnet/minecraft/client/renderer/block/model/BlockModel;"))
	private BlockModel markSkybox(ResourceLocation parentLocation, List<BlockElement> elements,
								  Map<String, Either<Material, String>> textureMap, Boolean hasAmbientOcclusion,
								  BlockModel.GuiLight guiLight, ItemTransforms transforms, List<ItemOverride> overrides,
								  Operation<BlockModel> original, JsonElement json) {
		BlockModel model = original.call(parentLocation, elements, textureMap, hasAmbientOcclusion, guiLight, transforms, overrides);
		JsonObject jsonObject = json.getAsJsonObject();
		if (jsonObject.has("crystalline_sky:atlas") &&
			ResourceLocation.parse(jsonObject.get("crystalline_sky:atlas").getAsString())
				.equals(CrystallineAtlases.SKYBOXES.texture)) {
			((BlockModel_Duck) model).crystalline_sky$setSkybox();
		}
		return model;
	}
}
