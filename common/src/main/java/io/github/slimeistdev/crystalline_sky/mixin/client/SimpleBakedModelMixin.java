package io.github.slimeistdev.crystalline_sky.mixin.client;

import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.SimpleBakedModel_Duck;
import net.minecraft.client.resources.model.SimpleBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SimpleBakedModel.class)
public class SimpleBakedModelMixin implements SimpleBakedModel_Duck {
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
}
