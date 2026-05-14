package io.github.slimeistdev.crystalline_sky.neoforge.skybox_model;

import io.github.slimeistdev.crystalline_sky.util.SharedRenderVariables;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SkyboxBakedModel<T extends BakedModel> extends BakedModelWrapper<T> {
	private static final IQuadTransformer TRANSFORMER = QuadTransformers.settingMaxEmissivity()
		.andThen(q -> {
			TextureAtlasSprite sprite = q.getSprite();
			float u0 = sprite.getU0();
			float v0 = sprite.getV0();
			int u1 = (int) (sprite.getU1() * 0xffff);
			int v1 = (int) (sprite.getV1() * 0xffff);
			int uv1 = (u1 << 16) | v1;

			var vertices = q.getVertices();
			for (int j = 0; j < 4; j++) {
				vertices[j * IQuadTransformer.STRIDE + IQuadTransformer.UV0] = Float.floatToRawIntBits(u0);
				vertices[j * IQuadTransformer.STRIDE + IQuadTransformer.UV0 + 1] = Float.floatToRawIntBits(v0);
				vertices[j * IQuadTransformer.STRIDE + IQuadTransformer.COLOR] = uv1;
			}
		});

	private record CacheKey(@Nullable BlockState state, @Nullable Direction side) {}
	private final Map<CacheKey, List<BakedQuad>> quadCache = new HashMap<>();

	public SkyboxBakedModel(T originalModel) {
		super(originalModel);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
		return TriState.FALSE;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	private static BakedQuad copyFullBright(BakedQuad quad) {
		var vertices = quad.getVertices();
		return new BakedQuad(
			Arrays.copyOf(vertices, vertices.length),
			-1, // no tint index
			quad.getDirection(),
			quad.getSprite(),
			false, // no shade
			false // no ao
		);
	}

	private static List<BakedQuad> process(List<BakedQuad> inputs) {
		return inputs.stream().map(SkyboxBakedModel::copyFullBright).peek(TRANSFORMER::processInPlace).toList();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
		CacheKey key = new CacheKey(state, side);
		return quadCache.computeIfAbsent(key, $ -> {
			SharedRenderVariables.pushShadeFullBright();

			List<BakedQuad> quads = super.getQuads(state, side, rand);
			List<BakedQuad> out = process(quads);

			SharedRenderVariables.popShadeFullBright();
			return out;
		});
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		return getQuads(state, side, rand);
	}
}
