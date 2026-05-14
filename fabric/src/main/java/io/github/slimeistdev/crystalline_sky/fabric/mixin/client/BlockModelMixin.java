package io.github.slimeistdev.crystalline_sky.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.slimeistdev.crystalline_sky.fabric.mixin_ducks.client.BlockModel_Duck;
import io.github.slimeistdev.crystalline_sky.fabric.mixin_ducks.client.SimpleBakedModel_Duck;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockModel.class)
public class BlockModelMixin implements BlockModel_Duck {
	@Unique
	private boolean crystalline_sky$is_skybox = false;

	@Override
	public boolean crystalline_sky$isSkybox() {
		return crystalline_sky$is_skybox;
	}

	@Override
	public void crystalline_sky$setSkybox() {
		crystalline_sky$is_skybox = true;
	}

	@WrapOperation(method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Z)Lnet/minecraft/client/resources/model/BakedModel;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/SimpleBakedModel$Builder;build()Lnet/minecraft/client/resources/model/BakedModel;"))
	private BakedModel transferSkybox(SimpleBakedModel.Builder instance, Operation<BakedModel> original) {
		BakedModel out = original.call(instance);
		if (crystalline_sky$isSkybox() && out instanceof SimpleBakedModel_Duck duck)
			duck.crystalline_sky$setSkybox();
		return out;
	}
}
