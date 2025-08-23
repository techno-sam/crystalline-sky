package io.github.slimeistdev.crystalline_sky.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;
import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkSkyLightProvider.class)
public abstract class ChunkSkyLightProviderMixin<M extends ChunkToNibbleArrayMap<M>, S extends LightStorage<M>> extends ChunkLightProvider<M, S> {
	@Shadow
	@Final
	private BlockPos.Mutable scratchPos;

	@Unique
	private static final int crystalline_sky$EMISSION_FROM_SKY_BLOCK = 1 << 22; // used to allow 15-level light to go down without reduction

	protected ChunkSkyLightProviderMixin(ChunkProvider chunkProvider, S lightStorage) {
		super(chunkProvider, lightStorage);
	}

	@Inject(method = "checkNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/SkyLightStorage;get(J)I"))
	private void emitFromSky(long blockPos, CallbackInfo ci) {
		BlockState state = getStateForLighting(scratchPos.set(blockPos));
		if (state.isOf(CrystallineBlocks.SKY) && ((LightStorageAccessor) lightStorage).crystalline_sky$callIsSectionInEnabledColumn(ChunkSectionPos.fromBlockPos(blockPos))) {
			enqueueIncrease(blockPos, QueueEntry.increaseLightFromEmission(15, isTrivialForLighting(state)) | crystalline_sky$EMISSION_FROM_SKY_BLOCK);
		}
	}

	@Definition(id = "lightLevel", local = @Local(type = int.class, argsOnly = true, ordinal = 0))
	@Expression("lightLevel - 1")
	@ModifyExpressionValue(
		method = "propagateIncrease",
		at = @At("MIXINEXTRAS:EXPRESSION"),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/SkyLightStorage;get(J)I"),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/ChunkSkyLightProvider;getStateForLighting(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0)
		)
	)
	private int unreducedDown1(int original, long blockPos, long packed, int lightLevel, @SuppressWarnings("LocalMayBeArgsOnly") @Local(ordinal = 0) LocalRef<Direction> direction, @Share("unreduced") LocalBooleanRef unreduced) {
		if (lightLevel == 15 && (packed & crystalline_sky$EMISSION_FROM_SKY_BLOCK) != 0 && direction.get() == Direction.DOWN) {
			unreduced.set(true);
			return lightLevel;
		} else {
			unreduced.set(false);
			return original;
		}
	}

	@WrapOperation(
		method = "propagateIncrease",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/chunk/light/ChunkSkyLightProvider;getOpacity(Lnet/minecraft/block/BlockState;)I",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/ChunkSkyLightProvider;getStateForLighting(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0)
		)
	)
	private int unreducedDown2(ChunkSkyLightProvider instance, BlockState blockState, Operation<Integer> original, @Share("unreduced") LocalBooleanRef unreduced) {
		if (unreduced.get()) {
			return blockState.getOpacity();
		}

		return original.call(instance, blockState);
	}

	@WrapOperation(
		method = "propagateIncrease",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/chunk/light/ChunkSkyLightProvider;enqueueIncrease(JJ)V"
		),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/ChunkSkyLightProvider;getStateForLighting(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0)
		)
	)
	private void passFlagOn(ChunkSkyLightProvider instance, long pos, long flags, Operation<Void> original, @Share("unreduced") LocalBooleanRef unreduced) {
		if (unreduced.get()) {
			flags |= crystalline_sky$EMISSION_FROM_SKY_BLOCK;
		}
		original.call(instance, pos, flags);
	}

	@Definition(id = "k", local = @Local(type = int.class, ordinal = 4))
	@Definition(id = "j", local = @Local(type = int.class, ordinal = 1))
	@Expression("k <= j - 1")
	@ModifyExpressionValue(
		method = "propagateDecrease",
		at = @At("MIXINEXTRAS:EXPRESSION"),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/SkyLightStorage;get(J)I"),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/SkyLightStorage;set(JI)V")
		)
	)
	private boolean cleanBelowSkyLight(boolean original, @Local(ordinal = 1) LocalIntRef j, @Local(ordinal = 4) LocalIntRef k, @SuppressWarnings("LocalMayBeArgsOnly") @Local LocalRef<Direction> direction) {
		return original || direction.get() == Direction.DOWN && j.get() == 15 && k.get() == 15;
	}
}
