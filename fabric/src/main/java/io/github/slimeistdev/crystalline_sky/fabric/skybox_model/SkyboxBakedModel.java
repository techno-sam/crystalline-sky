package io.github.slimeistdev.crystalline_sky.fabric.skybox_model;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.material.ShadeMode;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

public class SkyboxBakedModel extends ForwardingBakedModel {
	private static final RenderMaterial FULL_BRIGHT_MATERIAL;
	static {
		Renderer renderer = RendererAccess.INSTANCE.getRenderer();
		if (renderer == null) {
			FULL_BRIGHT_MATERIAL = null;
		} else {
			FULL_BRIGHT_MATERIAL = renderer.materialFinder()
				.shadeMode(ShadeMode.VANILLA)
				.ambientOcclusion(TriState.FALSE)
				.emissive(true)
				.disableDiffuse(true)
				.disableColorIndex(true)
				.find();
		}
	}

	public SkyboxBakedModel(BakedModel wrapped) {
		super(wrapped);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	// copied from net.fabricmc.fabric.impl.renderer.VanillaModelEncoder.emitBlockQuads
	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		QuadEmitter emitter = context.getEmitter();

		for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
			final Direction cullFace = ModelHelper.faceFromIndex(i);

			if (!context.hasTransform() && context.isFaceCulled(cullFace)) {
				// Skip entire quad list if possible
				continue;
			}

			final List<BakedQuad> quads = getQuads(state, cullFace, randomSupplier.get());

			for (final BakedQuad q : quads) {
				emitter.fromVanilla(q, FULL_BRIGHT_MATERIAL, cullFace);
				TextureAtlasSprite sprite = q.getSprite();
				float u0 = sprite.getU0();
				float v0 = sprite.getV0();
				int u1 = (int) (sprite.getU1() * 0xffff);
				int v1 = (int) (sprite.getV1() * 0xffff);
				int uv1 = (u1 << 16) | v1;
				for (int j = 0; j < 4; j++) {
					emitter.uv(j, u0, v0);
					emitter.color(j, uv1);
				}
				emitter.emit();
			}
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		super.emitItemQuads(stack, randomSupplier, context);
	}
}
