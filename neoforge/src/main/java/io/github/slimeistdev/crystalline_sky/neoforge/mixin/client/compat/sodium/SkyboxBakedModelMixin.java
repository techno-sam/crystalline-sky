package io.github.slimeistdev.crystalline_sky.neoforge.mixin.client.compat.sodium;

import io.github.slimeistdev.crystalline_sky.annotation.mixin.ConditionalMixin;
import io.github.slimeistdev.crystalline_sky.compat.Mods;
import io.github.slimeistdev.crystalline_sky.neoforge.skybox_model.SkyboxBakedModel;
import io.github.slimeistdev.crystalline_sky.util.SharedRenderVariables;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Supplier;

@ConditionalMixin(mods = Mods.SODIUM)
@Mixin(SkyboxBakedModel.class)
public class SkyboxBakedModelMixin implements FabricBakedModel {
	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		SharedRenderVariables.pushShadeFullBright();
		((AbstractBlockRenderContext) context).bufferDefaultModel((BakedModel) this, state);
		SharedRenderVariables.popShadeFullBright();
	}
}
