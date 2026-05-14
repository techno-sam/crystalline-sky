package io.github.slimeistdev.crystalline_sky.neoforge.skybox_model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SkyboxGeometryLoader implements IGeometryLoader<SkyboxWrappingGeometry<BlockModel>> {
	@Override
	public SkyboxWrappingGeometry<BlockModel> read(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
		jsonObject.remove("loader");
		BlockModel base = context.deserialize(jsonObject, BlockModel.class);
		return new SkyboxWrappingGeometry<>(base);
	}
}
