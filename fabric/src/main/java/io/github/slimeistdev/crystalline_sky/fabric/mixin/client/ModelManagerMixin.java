package io.github.slimeistdev.crystalline_sky.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.slimeistdev.crystalline_sky.fabric.events.RegisterMaterialAtlasesEvent;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin {
	@WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Ljava/util/Map;Lnet/minecraft/client/renderer/texture/TextureManager;)Lnet/minecraft/client/resources/model/AtlasSet;"))
	private AtlasSet addCustomAtlases(Map<ResourceLocation, ResourceLocation> atlasMap, TextureManager textureManager, Operation<AtlasSet> original) {
		return original.call(RegisterMaterialAtlasesEvent.gatherMaterialAtlases(atlasMap), textureManager);
	}
}
