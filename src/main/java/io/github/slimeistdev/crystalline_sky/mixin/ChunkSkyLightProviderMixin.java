package io.github.slimeistdev.crystalline_sky.mixin;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;
import net.minecraft.world.chunk.light.LightSourceView;
import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkSkyLightProvider.class)
public abstract class ChunkSkyLightProviderMixin<M extends ChunkToNibbleArrayMap<M>, S extends LightStorage<M>> extends ChunkLightProvider<M, S> {
	@Shadow
	@Final
	private BlockPos.Mutable scratchPos;

	protected ChunkSkyLightProviderMixin(ChunkProvider chunkProvider, S lightStorage) {
		super(chunkProvider, lightStorage);
	}

	@Inject(method = "checkNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/SkyLightStorage;get(J)I"))
	private void emitFromCrystallineSky(long blockPos, CallbackInfo ci) {
		BlockState state = getStateForLighting(scratchPos.set(blockPos));
		int level = CrystallineBlocks.getSkyLightLevel(state);
		if (level > 0 && ((LightStorageAccessor) lightStorage).crystalline_sky$callIsSectionInEnabledColumn(ChunkSectionPos.fromBlockPos(blockPos))) {
			enqueueIncrease(blockPos, QueueEntry.increaseLightFromEmission(level, isTrivialForLighting(state)));
		}
	}

	@Inject(method = "propagateLight", at = @At("RETURN"))
	private void propagateCrystallineSkyLight(ChunkPos chunkPos, CallbackInfo ci) {
		LightSourceView lightSourceView = this.chunkProvider.getChunk(chunkPos.x, chunkPos.z);
		if (!(lightSourceView instanceof Chunk chunk)) return;

		chunk.forEachBlockMatchingPredicate(CrystallineBlocks::isCrystallineSky, (pos, state) -> {
			enqueueIncrease(pos.asLong(), QueueEntry.increaseLightFromEmission(CrystallineBlocks.getSkyLightLevel(state), isTrivialForLighting(state)));
		});
	}
}
