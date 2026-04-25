package io.github.slimeistdev.crystalline_sky.fabric.skybox_model;

import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.SimpleBakedModel_Duck;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class SkyboxModelLoadingPlugin implements ModelLoadingPlugin {
	private SkyboxModelLoadingPlugin() {}

	@Override
	public void onInitializeModelLoader(Context pluginContext) {
		pluginContext.modifyModelAfterBake().register((model, ctx) -> {
			if (model instanceof SimpleBakedModel_Duck duck && duck.crystalline_sky$isSkybox())
				return new SkyboxBakedModel(model);

			return model;
		});
	}

	public static void register() {
		ModelLoadingPlugin.register(new SkyboxModelLoadingPlugin());
	}
}
