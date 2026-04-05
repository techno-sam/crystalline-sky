package io.github.slimeistdev.crystalline_sky.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.slimeistdev.crystalline_sky.infrastructure.WeepingStorage;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.ChunkSkyLight_Duck;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.lighting.SkyLightEngine;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// todone add ordinal to all name = "..." locals
@Mixin(SkyLightEngine.class)
public abstract class SkyLightEngineMixin<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>> extends LightEngine<M, S> {
	@Shadow
	@Final
	private BlockPos.MutableBlockPos mutablePos;

	@Shadow
	private static boolean isSourceLevel(int lightLevel) {
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

	protected SkyLightEngineMixin(LightChunkGetter chunkProvider, S lightStorage) {
		super(chunkProvider, lightStorage);
	}

	@Inject(method = "checkNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/SkyLightSectionStorage;getStoredLevel(J)I"))
	private void emitFromCrystallineSky(long blockPos, CallbackInfo ci) {
		BlockState state = getState(mutablePos.set(blockPos));
		int level = CrystallineBlocks.isWeepingSky(state)
			? 15
			: CrystallineBlocks.getSkyLightLevel(state);
		if (level > 0 && ((LayerLightSectionStorageAccessor) storage).crystalline_sky$callLightOnInSection(SectionPos.blockToSection(blockPos))) {
			enqueueIncrease(blockPos, QueueEntry.increaseLightFromEmission(level, isEmptyShape(state)));
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

		LightChunk chunk = chunkSource.getChunkForLighting(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
		if (chunk == null) return false;

		ChunkSkyLightSources chunkSkyLight = chunk.getSkyLightSources();
		if (chunkSkyLight == null) return false;

		int localX = SectionPos.sectionRelative(x);
		int localZ = SectionPos.sectionRelative(z);

		WeepingStorage weepingStorage = ((ChunkSkyLight_Duck) chunkSkyLight).crystalline_sky$getWeepingStorage();
		if (weepingStorage.isColumnEmpty(localX, localZ)) return false;

		return weepingStorage.isLit(localX, y, localZ);
	}

	@WrapOperation(method = "propagateDecrease", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/SkyLightEngine;enqueueDecrease(JJ)V"))
	private void decreaseTakesLightIntoAccount1(SkyLightEngine instance, long blockPos, long flags,
												Operation<Void> original, @Local(name = "k", ordinal = 2) int k) {
		// k is the old light level at blockPos
		BlockState state = getState(mutablePos.set(blockPos));
		int luminance = CrystallineBlocks.getSkyLightLevel(state);

		if (luminance < k) {
			original.call(instance, blockPos, flags);
		}

		if (luminance > 0) {
			enqueueIncrease(blockPos, QueueEntry.increaseLightFromEmission(luminance, isEmptyShape(state)));
		}
	}

	@WrapOperation(method = "propagateDecrease", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/SkyLightEngine;propagateFromEmptySections(JLnet/minecraft/core/Direction;IZI)V"))
	private void decreaseTakesLightIntoAccount2(SkyLightEngine instance, long blockPos, Direction direction,
												int lightLevel, boolean shouldIncrease, int emptySections,
												Operation<Void> original, @Local(name = "k", ordinal = 2) int k) {
		// k is the old light level at blockPos
		BlockState state = getState(mutablePos.set(blockPos));
		int luminance = CrystallineBlocks.getSkyLightLevel(state);

		if (luminance < k) {
			original.call(instance, blockPos, direction, lightLevel, shouldIncrease, emptySections);
		}

		if (luminance > 0) {
			original.call(instance, blockPos, direction, luminance, true, emptySections);
		}
	}

	@Inject(method = "propagateLightSources", at = @At("RETURN"))
	private void propagateCrystallineSkyLight(ChunkPos chunkPos, CallbackInfo ci) {
		LightChunk lightSourceView = this.chunkSource.getChunkForLighting(chunkPos.x, chunkPos.z);
		if (!(lightSourceView instanceof ChunkAccess chunk)) return;

		chunk.findBlocks(CrystallineBlocks::isCrystallineSky, (pos, state) -> {
			enqueueIncrease(pos.asLong(), QueueEntry.increaseLightFromEmission(CrystallineBlocks.getSkyLightLevel(state), isEmptyShape(state)));
		});
	}

	// NOTE: lowestSourceY becomes minY, and minY becomes bottomSectionY under parchment.
	// We're keeping the old names since it makes things more legible
	@Inject(method = "removeSourcesBelow", at = @At("HEAD"), cancellable = true)
	private void removeWeepingSourcesBelow(int x, int z, int lowestSourceY, int minY, CallbackInfo ci) {
		LightChunk chunk = chunkSource.getChunkForLighting(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
		if (chunk == null) return;

		ChunkSkyLightSources chunkSkyLight = chunk.getSkyLightSources();
		if (chunkSkyLight == null) return;

		int localX = SectionPos.sectionRelative(x);
		int localZ = SectionPos.sectionRelative(z);

		WeepingStorage weepingStorage = ((ChunkSkyLight_Duck) chunkSkyLight).crystalline_sky$getWeepingStorage();
		if (weepingStorage.isColumnEmpty(localX, localZ) && weepingStorage.hasNoUnlitSplits(localX, localZ)) return;

		// there's little chance of accidental mixin compatibility anyway, so just cancel
		ci.cancel();

		int sectionX = SectionPos.blockToSectionCoord(x);
		int sectionZ = SectionPos.blockToSectionCoord(z);
		int effectiveLowestSourceY = lowestSourceY <= minY ? Integer.MIN_VALUE : lowestSourceY;

		SkyLightSectionStorageAccessor lightStorageAccessor = (SkyLightSectionStorageAccessor) this.storage;

		RunIter: for (WeepingStorage.Run run : weepingStorage.iterateUnlitRuns(localX, localZ, effectiveLowestSourceY)) {
			// check lighting state of run
			int topUnlit = run.topY();
			int bottomUnlit = run.bottomY();

			for (int sectionY = SectionPos.blockToSectionCoord(topUnlit); lightStorageAccessor.crystalline_sky$isAboveMinHeight(sectionY); sectionY--) {
				if (lightStorageAccessor.crystalline_sky$hasSection(SectionPos.asLong(sectionX, sectionY, sectionZ))) {
					int minYInSection = SectionPos.sectionToBlockCoord(sectionY);
					int maxYInSection = minYInSection + 15;

					if (maxYInSection < bottomUnlit) { // todone break outer early if we're below bottomUnlit
						break;
					}

					for (int it_y = Math.min(maxYInSection, topUnlit); it_y >= Math.max(minYInSection, bottomUnlit); it_y--) {
						long it_pos = BlockPos.asLong(x, it_y, z);

						if (CrystallineBlocks.isCrystallineSky(chunk.getBlockState(mutablePos.set(it_pos)))) {
							continue;
						}

						if (!isSourceLevel(lightStorageAccessor.crystalline_sky$get(it_pos))) {
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

	// NOTE: lowestSourceY becomes maxY, and minY becomes bottomSectionY under parchment.
	// We're keeping the old names since it makes things more legible
	@Inject(method = "addSourcesAbove", at = @At("RETURN"))
	private void addAdditionalWeepingSourcesAbove(int x, int z, int lowestSourceY, int minY, CallbackInfo ci,
												  @Local(name = "maxAdjacentLowestSourceY", ordinal = 6) int maxAdjacentLowestSourceY) {
		LightChunk chunk = chunkSource.getChunkForLighting(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
		if (chunk == null) return;

		ChunkSkyLightSources chunkSkyLight = chunk.getSkyLightSources();
		if (chunkSkyLight == null) return;

		int localX = SectionPos.sectionRelative(x);
		int localZ = SectionPos.sectionRelative(z);

		WeepingStorage weepingStorage = ((ChunkSkyLight_Duck) chunkSkyLight).crystalline_sky$getWeepingStorage();
		if (weepingStorage.isColumnEmpty(localX, localZ)) {
			weepingStorage.clearTempRunSplits(localX, localZ);
			return;
		}

		int sectionX = SectionPos.blockToSectionCoord(x);
		int sectionZ = SectionPos.blockToSectionCoord(z);
		int effectiveLowestSourceY = lowestSourceY <= minY ? Integer.MIN_VALUE : lowestSourceY;

		if (effectiveLowestSourceY == Integer.MIN_VALUE) {
			weepingStorage.clearTempRunSplits(localX, localZ);
			return;
		}

		SkyLightSectionStorageAccessor lightStorageAccessor = (SkyLightSectionStorageAccessor) this.storage;

		RunIter: for (WeepingStorage.Run run : weepingStorage.iterateLitRuns(localX, localZ, effectiveLowestSourceY)) {
			if (run.topY() == Integer.MAX_VALUE) {
				if (run.bottomY() == effectiveLowestSourceY) {
					continue;
				} else {
					run = run.withTopY(effectiveLowestSourceY - 1);
				}
			}

			int lowestRelevantY = Math.max(run.bottomY(), minY);
			for (long it_packedSection = SectionPos.asLong(sectionX, SectionPos.blockToSectionCoord(lowestRelevantY), sectionZ);
				!lightStorageAccessor.crystalline_sky$isAtOrAboveTopmostSection(it_packedSection);
				it_packedSection = SectionPos.offset(it_packedSection, Direction.UP)
			) {
				if (lightStorageAccessor.crystalline_sky$hasSection(it_packedSection)) {
					int minYInSection = SectionPos.sectionToBlockCoord(SectionPos.y(it_packedSection));
					int maxYInSection = minYInSection + 15;

					if (minYInSection > run.topY()) { // todone break outer early if we're above run.topY()
						break;
					}

					for (int it_y = Math.max(minYInSection, lowestRelevantY); it_y <= Math.min(maxYInSection, run.topY()); it_y++) {
						long it_pos = BlockPos.asLong(x, it_y, z);

						if (CrystallineBlocks.isCrystallineSky(chunk.getBlockState(mutablePos.set(it_pos)))) {
							continue;
						}

						if (isSourceLevel(lightStorageAccessor.crystalline_sky$get(it_pos))) {
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

	@Inject(method = "propagateLightSources", at = @At("RETURN"))
	private void propagateWeepingLight(ChunkPos chunkPos, CallbackInfo ci,
									   @Local(name = "sourcesO", ordinal = 0) ChunkSkyLightSources sourcesO,
									   @Local(name = "sourcesZN", ordinal = 1) ChunkSkyLightSources sourcesZN,
									   @Local(name = "sourcesZP", ordinal = 2) ChunkSkyLightSources sourcesZP,
									   @Local(name = "sourcesXN", ordinal = 3) ChunkSkyLightSources sourcesXN,
									   @Local(name = "sourcesXP", ordinal = 4) ChunkSkyLightSources sourcesXP
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

		long packedZeroPos = SectionPos.getZeroNode(chunkPos.x, chunkPos.z);
		SkyLightSectionStorageAccessor lightStorageAccessor = (SkyLightSectionStorageAccessor) this.storage;

		int topSectionY = lightStorageAccessor.crystalline_sky$getTopSectionForColumn(packedZeroPos);
		int bottomSectionY = lightStorageAccessor.crystalline_sky$getMinSectionY();
		int baseX = SectionPos.sectionToBlockCoord(chunkPos.x);
		int baseZ = SectionPos.sectionToBlockCoord(chunkPos.z);

		int minY = SectionPos.sectionToBlockCoord(bottomSectionY);

		DataLayer[] lightArrays = new DataLayer[topSectionY - bottomSectionY];
		for (int sectionY = topSectionY - 1; sectionY >= bottomSectionY; sectionY--) {
			long packedSectionPos = SectionPos.asLong(chunkPos.x, sectionY, chunkPos.z);
			lightArrays[sectionY - bottomSectionY] = lightStorageAccessor.crystalline_sky$method_51547(packedSectionPos);
		}

		for (int localZ = 0; localZ < 16; localZ++) {
			for (int localX = 0; localX < 16; localX++) {
				if (weepingStorage.isColumnEmpty(localX, localZ)) continue;

				int lowestSourceY = sourcesO.getLowestSourceY(localX, localZ);
				int effectiveLowestSourceY = lowestSourceY <= minY ? Integer.MIN_VALUE : lowestSourceY;
				if (effectiveLowestSourceY == Integer.MIN_VALUE) continue;

				int minSourceY_ZN = localZ == 0 ? sourcesZN.getLowestSourceY(localX, 15) : sourcesO.getLowestSourceY(localX, localZ - 1);
				int minSourceY_ZP = localZ == 15 ? sourcesZP.getLowestSourceY(localX, 0) : sourcesO.getLowestSourceY(localX, localZ + 1);
				int minSourceY_XN = localX == 0 ? sourcesXN.getLowestSourceY(15, localZ) : sourcesO.getLowestSourceY(localX - 1, localZ);
				int minSourceY_XP = localX == 15 ? sourcesXP.getLowestSourceY(0, localZ) : sourcesO.getLowestSourceY(localX + 1, localZ);
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
					for (int sectionY = SectionPos.blockToSectionCoord(lowestRelevantY); sectionY <= topSectionY - 1; sectionY++) {
						DataLayer lightArray = lightArrays[sectionY - bottomSectionY];
						if (lightArray == null) continue;

						int minYInSection = SectionPos.sectionToBlockCoord(sectionY);
						int maxYInSection = minYInSection + 15;

						if (minYInSection > run.topY()) { // todone break outer early if we're above run.topY()
							break;
						}

						for (int it_y = Math.max(minYInSection, lowestRelevantY); it_y <= Math.min(maxYInSection, run.topY()); it_y++) {
							lightArray.set(localX, SectionPos.sectionRelative(it_y), localZ, 15);

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
