package io.github.slimeistdev.crystalline_sky.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.slimeistdev.crystalline_sky.infrastructure.WeepingStorage;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.ChunkSkyLight_Duck;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.ChunkSkyLight;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;
import net.minecraft.world.chunk.light.LightSourceView;
import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// todo replace all name = "..." locals with ordinal
@Mixin(ChunkSkyLightProvider.class)
public abstract class ChunkSkyLightProviderMixin<M extends ChunkToNibbleArrayMap<M>, S extends LightStorage<M>> extends ChunkLightProvider<M, S> {
	@Shadow
	@Final
	private BlockPos.Mutable scratchPos;

	@Shadow
	private static boolean isMaxLightLevel(int lightLevel) {
		throw new RuntimeException("Mixin failed to apply");
	}

	@Shadow
	@Final
	private static long REMOVE_TOP_SKY_SOURCE_ENTRY;

	@Shadow
	@Final
	private static long REMOVE_SKY_SOURCE_ENTRY;

	@Shadow
	@Final
	private static long ADD_SKY_SOURCE_ENTRY;

	protected ChunkSkyLightProviderMixin(ChunkProvider chunkProvider, S lightStorage) {
		super(chunkProvider, lightStorage);
	}

	@Inject(method = "checkNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/SkyLightStorage;get(J)I"))
	private void emitFromCrystallineSky(long blockPos, CallbackInfo ci) {
		BlockState state = getStateForLighting(scratchPos.set(blockPos));
		int level = CrystallineBlocks.isWeepingSky(state)
			? 15
			: CrystallineBlocks.getSkyLightLevel(state);
		if (level > 0 && ((LightStorageAccessor) lightStorage).crystalline_sky$callIsSectionInEnabledColumn(ChunkSectionPos.fromBlockPos(blockPos))) {
			enqueueIncrease(blockPos, QueueEntry.increaseLightFromEmission(level, isTrivialForLighting(state)));
		}
	}

	@Definition(id = "y", local = @Local(type = int.class, name = "y"))
	@Definition(id = "lowestSourceY", local = @Local(type = int.class, name = "lowestSourceY"))
	@Expression("y >= lowestSourceY")
	@WrapOperation(method = "checkNode", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean weepBelowTheLowest(int y, int lowestSourceY, Operation<Boolean> original, @Local(name = "x") int x, @Local(name = "z") int z) {
		if (original.call(y, lowestSourceY)) {
			return true;
		}

		LightSourceView chunk = chunkProvider.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
		if (chunk == null) return false;

		ChunkSkyLight chunkSkyLight = chunk.getChunkSkyLight();
		if (chunkSkyLight == null) return false;

		int localX = ChunkSectionPos.getLocalCoord(x);
		int localZ = ChunkSectionPos.getLocalCoord(z);

		WeepingStorage weepingStorage = ((ChunkSkyLight_Duck) chunkSkyLight).crystalline_sky$getWeepingStorage();
		if (weepingStorage.isColumnEmpty(localX, localZ)) return false;

		return weepingStorage.isLit(localX, y, localZ);
	}

	@Inject(method = "propagateLight", at = @At("RETURN"))
	private void propagateCrystallineSkyLight(ChunkPos chunkPos, CallbackInfo ci) {
		LightSourceView lightSourceView = this.chunkProvider.getChunk(chunkPos.x, chunkPos.z);
		if (!(lightSourceView instanceof Chunk chunk)) return;

		chunk.forEachBlockMatchingPredicate(CrystallineBlocks::isCrystallineSky, (pos, state) -> {
			enqueueIncrease(pos.asLong(), QueueEntry.increaseLightFromEmission(CrystallineBlocks.getSkyLightLevel(state), isTrivialForLighting(state)));
		});
	}

	@Inject(method = "removeSourcesBelow", at = @At("HEAD"), cancellable = true)
	private void removeWeepingSourcesBelow(int x, int z, int lowestSourceY, int minY, CallbackInfo ci) {
		LightSourceView chunk = chunkProvider.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
		if (chunk == null) return;

		ChunkSkyLight chunkSkyLight = chunk.getChunkSkyLight();
		if (chunkSkyLight == null) return;

		int localX = ChunkSectionPos.getLocalCoord(x);
		int localZ = ChunkSectionPos.getLocalCoord(z);

		WeepingStorage weepingStorage = ((ChunkSkyLight_Duck) chunkSkyLight).crystalline_sky$getWeepingStorage();
		if (weepingStorage.isColumnEmpty(localX, localZ) && weepingStorage.hasNoUnlitSplits(localX, localZ)) return;

		// there's little chance of accidental mixin compatibility anyway, so just cancel
		ci.cancel();

		int sectionX = ChunkSectionPos.getSectionCoord(x);
		int sectionZ = ChunkSectionPos.getSectionCoord(z);
		int effectiveLowestSourceY = lowestSourceY <= minY ? Integer.MIN_VALUE : lowestSourceY;

		SkyLightStorageAccessor lightStorageAccessor = (SkyLightStorageAccessor) this.lightStorage;

		RunIter: for (WeepingStorage.Run run : weepingStorage.iterateUnlitRuns(localX, localZ, effectiveLowestSourceY)) {
			// check lighting state of run
			int topUnlit = run.topY();
			int bottomUnlit = run.bottomY();

			for (int sectionY = ChunkSectionPos.getSectionCoord(topUnlit); lightStorageAccessor.crystalline_sky$isAboveMinHeight(sectionY); sectionY--) {
				if (lightStorageAccessor.crystalline_sky$hasSection(ChunkSectionPos.asLong(sectionX, sectionY, sectionZ))) {
					int minYInSection = ChunkSectionPos.getBlockCoord(sectionY);
					int maxYInSection = minYInSection + 15;

					if (maxYInSection < bottomUnlit) { // todone break outer early if we're below bottomUnlit
						break;
					}

					for (int it_y = Math.min(maxYInSection, topUnlit); it_y >= Math.max(minYInSection, bottomUnlit); it_y--) {
						long it_pos = BlockPos.asLong(x, it_y, z);

						if (CrystallineBlocks.isCrystallineSky(chunk.getBlockState(scratchPos.set(it_pos)))) {
							continue;
						}

						if (!isMaxLightLevel(lightStorageAccessor.crystalline_sky$get(it_pos))) {
							continue RunIter;
						}

						lightStorageAccessor.crystalline_sky$set(it_pos, 0);
						// todone which of these is correct?
						enqueueDecrease(it_pos, it_y == topUnlit ? REMOVE_TOP_SKY_SOURCE_ENTRY : REMOVE_SKY_SOURCE_ENTRY);
						//enqueueDecrease(it_pos, REMOVE_SKY_SOURCE_ENTRY);
					}
				}
			}
		}
	}

	@Inject(method = "addSourcesAbove", at = @At("RETURN"))
	private void addAdditionalWeepingSourcesAbove(int x, int z, int lowestSourceY, int minY, CallbackInfo ci,
												  @Local(name = "maxAdjacentLowestSourceY") int maxAdjacentLowestSourceY) {
		LightSourceView chunk = chunkProvider.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
		if (chunk == null) return;

		ChunkSkyLight chunkSkyLight = chunk.getChunkSkyLight();
		if (chunkSkyLight == null) return;

		int localX = ChunkSectionPos.getLocalCoord(x);
		int localZ = ChunkSectionPos.getLocalCoord(z);

		WeepingStorage weepingStorage = ((ChunkSkyLight_Duck) chunkSkyLight).crystalline_sky$getWeepingStorage();
		if (weepingStorage.isColumnEmpty(localX, localZ)) {
			weepingStorage.clearTempRunSplits(localX, localZ);
			return;
		}

		int sectionX = ChunkSectionPos.getSectionCoord(x);
		int sectionZ = ChunkSectionPos.getSectionCoord(z);
		int effectiveLowestSourceY = lowestSourceY <= minY ? Integer.MIN_VALUE : lowestSourceY;

		if (effectiveLowestSourceY == Integer.MIN_VALUE) {
			weepingStorage.clearTempRunSplits(localX, localZ);
			return;
		}

		SkyLightStorageAccessor lightStorageAccessor = (SkyLightStorageAccessor) this.lightStorage;

		RunIter: for (WeepingStorage.Run run : weepingStorage.iterateLitRuns(localX, localZ, effectiveLowestSourceY)) {
			if (run.topY() == Integer.MAX_VALUE) {
				if (run.bottomY() == effectiveLowestSourceY) {
					continue;
				} else {
					run = run.withTopY(effectiveLowestSourceY - 1);
				}
			}

			int lowestRelevantY = Math.max(run.bottomY(), minY);
			for (long it_packedSection  = ChunkSectionPos.asLong(sectionX, ChunkSectionPos.getSectionCoord(lowestRelevantY), sectionZ);
				!lightStorageAccessor.crystalline_sky$isAtOrAboveTopmostSection(it_packedSection);
				it_packedSection = ChunkSectionPos.offset(it_packedSection, Direction.UP)
			) {
				if (lightStorageAccessor.crystalline_sky$hasSection(it_packedSection)) {
					int minYInSection = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(it_packedSection));
					int maxYInSection = minYInSection + 15;

					if (minYInSection > run.topY()) { // todone break outer early if we're above run.topY()
						break;
					}

					for (int it_y = Math.max(minYInSection, lowestRelevantY); it_y <= Math.min(maxYInSection, run.topY()); it_y++) {
						long it_pos = BlockPos.asLong(x, it_y, z);

						if (CrystallineBlocks.isCrystallineSky(chunk.getBlockState(scratchPos.set(it_pos)))) {
							continue;
						}

						if (isMaxLightLevel(lightStorageAccessor.crystalline_sky$get(it_pos))) {
							continue RunIter;
						}

						lightStorageAccessor.crystalline_sky$set(it_pos, 15);
						if (it_y < maxAdjacentLowestSourceY || it_y == lowestRelevantY) {
							enqueueIncrease(it_pos, ADD_SKY_SOURCE_ENTRY);
						}
					}
				}
			}
		}

		weepingStorage.clearTempRunSplits(localX, localZ);
	}
}
