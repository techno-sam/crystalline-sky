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
import net.minecraft.world.chunk.ChunkNibbleArray;
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

// todone add ordinal to all name = "..." locals
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

	@SuppressWarnings("LocalMayBeArgsOnly")
	@Definition(id = "y", local = @Local(type = int.class, name = "y", ordinal = 1))
	@Definition(id = "lowestSourceY", local = @Local(type = int.class, name = "lowestSourceY", ordinal = 3))
	@Expression("y >= lowestSourceY")
	@WrapOperation(method = "checkNode", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean weepBelowTheLowest(int y, int lowestSourceY, Operation<Boolean> original, @Local(name = "x", ordinal = 0) int x, @Local(name = "z", ordinal = 2) int z) {
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

	@WrapOperation(method = "propagateDecrease", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/ChunkSkyLightProvider;enqueueDecrease(JJ)V"))
	private void decreaseTakesLightIntoAccount1(ChunkSkyLightProvider instance, long blockPos, long flags,
												Operation<Void> original, @Local(name = "k", ordinal = 2) int k) {
		// k is the old light level at blockPos
		BlockState state = getStateForLighting(scratchPos.set(blockPos));
		int luminance = CrystallineBlocks.getSkyLightLevel(state);

		if (luminance < k) {
			original.call(instance, blockPos, flags);
		}

		if (luminance > 0) {
			enqueueIncrease(blockPos, QueueEntry.increaseLightFromEmission(luminance, isTrivialForLighting(state)));
		}
	}

	@WrapOperation(method = "propagateDecrease", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/ChunkSkyLightProvider;propagateFromEmptySections(JLnet/minecraft/util/math/Direction;IZI)V"))
	private void decreaseTakesLightIntoAccount2(ChunkSkyLightProvider instance, long blockPos, Direction direction,
												int lightLevel, boolean shouldIncrease, int emptySections,
												Operation<Void> original, @Local(name = "k", ordinal = 2) int k) {
		// k is the old light level at blockPos
		BlockState state = getStateForLighting(scratchPos.set(blockPos));
		int luminance = CrystallineBlocks.getSkyLightLevel(state);

		if (luminance < k) {
			original.call(instance, blockPos, direction, lightLevel, shouldIncrease, emptySections);
		}

		if (luminance > 0) {
			original.call(instance, blockPos, direction, luminance, true, emptySections);
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
												  @Local(name = "maxAdjacentLowestSourceY", ordinal = 6) int maxAdjacentLowestSourceY) {
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

	@Inject(method = "propagateLight", at = @At("RETURN"))
	private void propagateWeepingLight(ChunkPos chunkPos, CallbackInfo ci,
									   @Local(name = "sourcesO", ordinal = 0) ChunkSkyLight sourcesO,
									   @Local(name = "sourcesZN", ordinal = 1) ChunkSkyLight sourcesZN,
									   @Local(name = "sourcesZP", ordinal = 2) ChunkSkyLight sourcesZP,
									   @Local(name = "sourcesXN", ordinal = 3) ChunkSkyLight sourcesXN,
									   @Local(name = "sourcesXP", ordinal = 4) ChunkSkyLight sourcesXP
	) {
		WeepingStorage weepingStorage = ((ChunkSkyLight_Duck) sourcesO).crystalline_sky$getWeepingStorage();
		if (weepingStorage == null) return;

		boolean hasAny = false;
		Outer: for (int localX = 0; localX < 16; localX++) {
			for (int localZ = 0; localZ < 16; localZ++) {
				if (!weepingStorage.isColumnEmpty(localX, localZ)) {
					hasAny = true;
					break Outer;
				}
			}
		}
		if (!hasAny)
			return;

		long packedZeroPos = ChunkSectionPos.withZeroY(chunkPos.x, chunkPos.z);
		SkyLightStorageAccessor lightStorageAccessor = (SkyLightStorageAccessor) this.lightStorage;

		int topSectionY = lightStorageAccessor.crystalline_sky$getTopSectionForColumn(packedZeroPos);
		int bottomSectionY = lightStorageAccessor.crystalline_sky$getMinSectionY();
		int baseX = ChunkSectionPos.getBlockCoord(chunkPos.x);
		int baseZ = ChunkSectionPos.getBlockCoord(chunkPos.z);

		int minY = ChunkSectionPos.getBlockCoord(bottomSectionY);

		ChunkNibbleArray[] lightArrays = new ChunkNibbleArray[topSectionY - bottomSectionY];
		for (int sectionY = topSectionY - 1; sectionY >= bottomSectionY; sectionY--) {
			long packedSectionPos = ChunkSectionPos.asLong(chunkPos.x, sectionY, chunkPos.z);
			lightArrays[sectionY - bottomSectionY] = lightStorageAccessor.crystalline_sky$method_51547(packedSectionPos);
		}

		for (int localZ = 0; localZ < 16; localZ++) {
			for (int localX = 0; localX < 16; localX++) {
				if (weepingStorage.isColumnEmpty(localX, localZ)) continue;

				int lowestSourceY = sourcesO.get(localX, localZ);
				int effectiveLowestSourceY = lowestSourceY <= minY ? Integer.MIN_VALUE : lowestSourceY;
				if (effectiveLowestSourceY == Integer.MIN_VALUE) continue;

				int minSourceY_ZN = localZ == 0 ? sourcesZN.get(localX, 15) : sourcesO.get(localX, localZ - 1);
				int minSourceY_ZP = localZ == 15 ? sourcesZP.get(localX, 0) : sourcesO.get(localX, localZ + 1);
				int minSourceY_XN = localX == 0 ? sourcesXN.get(15, localZ) : sourcesO.get(localX - 1, localZ);
				int minSourceY_XP = localX == 15 ? sourcesXP.get(0, localZ) : sourcesO.get(localX + 1, localZ);
				int minSourceY_max = Math.max(Math.max(minSourceY_ZN, minSourceY_ZP), Math.max(minSourceY_XN, minSourceY_XP));

				for (WeepingStorage.Run run : weepingStorage.iterateLitRuns(localX, localZ, effectiveLowestSourceY)) {
					if (run.topY() == Integer.MAX_VALUE) {
						if (run.bottomY() == effectiveLowestSourceY) {
							continue;
						} else {
							run = run.withTopY(effectiveLowestSourceY - 1);
						}
					}

					int lowestRelevantY = Math.max(run.bottomY(), minY);
					for (int sectionY = ChunkSectionPos.getSectionCoord(lowestRelevantY); sectionY <= topSectionY - 1; sectionY++) {
						ChunkNibbleArray lightArray = lightArrays[sectionY - bottomSectionY];
						if (lightArray == null) continue;

						int minYInSection = ChunkSectionPos.getBlockCoord(sectionY);
						int maxYInSection = minYInSection + 15;

						if (minYInSection > run.topY()) { // todone break outer early if we're above run.topY()
							break;
						}

						for (int it_y = Math.max(minYInSection, lowestRelevantY); it_y <= Math.min(maxYInSection, run.topY()); it_y++) {
							lightArray.set(localX, ChunkSectionPos.getLocalCoord(it_y), localZ, 15);

							if (it_y == lowestRelevantY || it_y < minSourceY_max) {
								long it_pos = BlockPos.asLong(baseX + localX, it_y, baseZ + localZ);
								enqueueIncrease(
									it_pos,
									QueueEntry.increaseSkySourceInDirections(
										it_y == lowestRelevantY,
										it_y < minSourceY_ZN,
										it_y < minSourceY_ZP,
										it_y < minSourceY_XN,
										it_y < minSourceY_XP
									)
								);
							}
						}
					}
				}
			}
		}
	}
}
