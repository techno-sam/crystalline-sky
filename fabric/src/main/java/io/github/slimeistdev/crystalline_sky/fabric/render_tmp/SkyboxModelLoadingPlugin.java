package io.github.slimeistdev.crystalline_sky.fabric.render_tmp;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

public class SkyboxModelLoadingPlugin implements ModelLoadingPlugin {
	private SkyboxModelLoadingPlugin() {}

	@Override
	public void onInitializeModelLoader(Context pluginContext) {
		pluginContext.modifyModelAfterBake().register((model, ctx) -> {
			if (model == null) return null;

			ResourceLocation id = ctx.resourceId();
			if (id == null) {
				id = ctx.topLevelId().id();
			}
			if (!id.equals(CrystallineSky.id("block/skybox_test"))) return model;

			return new SkyboxBakedModel(model);
		});
	}

	public static void register() {
		ModelLoadingPlugin.register(new SkyboxModelLoadingPlugin());
	}
}
