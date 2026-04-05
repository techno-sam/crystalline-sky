package io.github.slimeistdev.crystalline_sky.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LightEngine.class)
public class LightEngineMixin {
	@WrapMethod(method = "hasDifferentLightProperties")
	private static boolean updateSky(BlockGetter blockView, BlockPos pos, BlockState oldState, BlockState newState, Operation<Boolean> original) {
		return original.call(blockView, pos, oldState, newState)
			|| (CrystallineBlocks.getSkyLightLevel(oldState) != CrystallineBlocks.getSkyLightLevel(newState))
			|| (CrystallineBlocks.isWeepingSky(oldState) != CrystallineBlocks.isWeepingSky(newState));
	}
}
