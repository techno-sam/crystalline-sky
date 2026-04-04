package io.github.slimeistdev.crystalline_sky.mixin;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.infrastructure.IntWeepingStorage;
import io.github.slimeistdev.crystalline_sky.infrastructure.MutableWeepingScanner;
import io.github.slimeistdev.crystalline_sky.infrastructure.WeepingStorage;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.ChunkSkyLight_Duck;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.light.ChunkSkyLight;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSkyLight.class)
public abstract class ChunkSkyLightMixin implements ChunkSkyLight_Duck {
	@Shadow
	@Final
	private int minY;

	@Shadow
	@Final
	private BlockPos.Mutable reusableBlockPos1;

	@Shadow
	private static boolean faceBlocksLight(BlockView blockView, BlockPos upperPos, BlockState upperState, BlockPos lowerPos, BlockState lowerState) {
		throw new RuntimeException("Mixin failed to apply");
	}

	@Shadow
	@Final
	private BlockPos.Mutable reusableBlockPos2;

	@Unique
	@Final
	@Mutable
	private IntWeepingStorage crystalline_sky$weepingStorage;

	@Unique
	@Final
	@Mutable
	private MutableWeepingScanner crystalline_sky$weepingScanner;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(HeightLimitView heightLimitView, CallbackInfo ci) {
		crystalline_sky$weepingStorage = new IntWeepingStorage(minY + 1);
		crystalline_sky$weepingScanner = new MutableWeepingScanner(minY + 1);
	}

	@Override
	public WeepingStorage crystalline_sky$getWeepingStorage() {
		return crystalline_sky$weepingStorage;
	}

	@Inject(method = "refreshSurfaceY", at = @At("HEAD"))
	private void refreshWeepingSurfaceY(Chunk chunk, CallbackInfo ci) {
		int highestNonEmptySectionIndex = chunk.getHighestNonEmptySection();
		if (highestNonEmptySectionIndex == -1) {
			crystalline_sky$weepingStorage.clear();
		} else {
			int topY = ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(highestNonEmptySectionIndex) + 1);
			int weepingMinY = minY + 1;

			ShortList data = new ShortArrayList();

			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
					data.clear();

					BlockPos.Mutable topPos = reusableBlockPos1.set(x, topY, z);
					BlockPos.Mutable bottomPos = reusableBlockPos2.set(x, topY - 1, z);
					BlockState topState = Blocks.AIR.getDefaultState();

					short foundSky = -1;

					for (int sectionIndex = highestNonEmptySectionIndex; sectionIndex >= 0; sectionIndex--) {
						ChunkSection section = chunk.getSection(sectionIndex);

						if (section.isEmpty()) {
							topState = Blocks.AIR.getDefaultState();
							topPos.setY(ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(sectionIndex)));
							bottomPos.setY(topPos.getY() - 1);
						} else {
							for (int y = 15; y >= 0; y--) {
								BlockState bottomState = section.getBlockState(x, y, z);

								if (CrystallineBlocks.isWeepingSky(bottomState)) {
									if (foundSky == -1) {
										foundSky = (short) (bottomPos.getY() - weepingMinY);
									}
								} else if (faceBlocksLight(chunk, topPos, topState, bottomPos, bottomState)) {
									if (foundSky != -1) {
										short lastLitY = (short) (bottomPos.getY() - weepingMinY + 1);
										data.add(foundSky);
										data.add(lastLitY);
										foundSky = -1;
									}
								}

								topState = bottomState;
								topPos.set(bottomPos);
								bottomPos.move(Direction.DOWN);
							}
						}
					}

					if (foundSky != -1) {
						data.add(foundSky);
						data.add((short) -1);
					}

					crystalline_sky$weepingStorage.set(x, z, data);
				}
			}
		}
	}

	@Inject(method = "isSkyLightAccessible(Lnet/minecraft/world/BlockView;III)Z", at = @At("HEAD"))
	private void updateWeepingSkyState(BlockView blockView, int localX, int y, int localZ, CallbackInfoReturnable<Boolean> cir) {
		BlockPos pos = reusableBlockPos1.set(localX, y, localZ);
		BlockState state = blockView.getBlockState(pos);

		crystalline_sky$weepingScanner.setBlockView(blockView);

		int weepingMinY = minY + 1;

		if (state.isAir()) {
			crystalline_sky$weepingStorage.insertAir(localX, y - weepingMinY, localZ, crystalline_sky$weepingScanner);
		} else if (CrystallineBlocks.isWeepingSky(state)) {
			crystalline_sky$weepingStorage.insertSky(localX, y - weepingMinY, localZ, crystalline_sky$weepingScanner);
		} else if (state.isOpaque()) {
			crystalline_sky$weepingStorage.insertSolid(localX, y - weepingMinY, localZ, crystalline_sky$weepingScanner);
		}

		CrystallineSky.LOG.debug("Weeping sky ranges for local column [{}, {}] in {}:", localX, localZ, blockView);
		crystalline_sky$weepingStorage.debugState(localX, localZ, CrystallineSky.LOG::debug);

		crystalline_sky$weepingScanner.setBlockView(null);
	}
}
