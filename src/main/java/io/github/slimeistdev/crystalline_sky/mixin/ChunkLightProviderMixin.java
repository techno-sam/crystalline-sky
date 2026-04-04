package io.github.slimeistdev.crystalline_sky.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkLightProvider.class)
public class ChunkLightProviderMixin {
	@WrapMethod(method = "needsLightUpdate")
	private static boolean updateSky(BlockView blockView, BlockPos pos, BlockState oldState, BlockState newState, Operation<Boolean> original) {
		return original.call(blockView, pos, oldState, newState)
			|| (CrystallineBlocks.getSkyLightLevel(oldState) != CrystallineBlocks.getSkyLightLevel(newState))
			|| (CrystallineBlocks.isWeepingSky(oldState) != CrystallineBlocks.isWeepingSky(newState));
	}
}
