package io.github.slimeistdev.crystalline_sky.neoforge.skybox_model;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SkyboxWrappingGeometry<T extends UnbakedModel> implements IUnbakedGeometry<SkyboxWrappingGeometry<T>> {
	private final T wrapped;

	public SkyboxWrappingGeometry(T wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		return new SkyboxBakedModel<>(Objects.requireNonNull(wrapped.bake(baker, spriteGetter, modelState)));
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
		wrapped.resolveParents(modelGetter);
	}
}
