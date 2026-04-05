package io.github.slimeistdev.crystalline_sky.infrastructure;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.lighting.LightEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MutableWeepingScanner implements WeepingStorage.WeepingScanner {
	private @Nullable BlockGetter blockView;
	private final int minY;

	private final BlockPos.MutableBlockPos reusablePos1 = new BlockPos.MutableBlockPos();
	private final BlockPos.MutableBlockPos reusablePos2 = new BlockPos.MutableBlockPos();

	public MutableWeepingScanner(int minY) {
		this.minY = minY;
	}

	public void setBlockView(@Nullable BlockGetter blockView) {
		this.blockView = blockView;
	}

	private int transformInputY(int y) {
		return y + minY;
	}

	private int transformOutputY(int y) {
		return y == Integer.MIN_VALUE ? -1 : y - minY;
	}

	private @NotNull BlockGetter checkBlockView() {
		if (blockView == null) {
			throw new IllegalStateException("blockView is not set");
		} else {
			return blockView;
		}
	}

	@Override
	public int scan(int localX, int yMax, int localZ) {
		BlockGetter blockView = checkBlockView();

		int actualYMax = transformInputY(yMax);

		BlockPos.MutableBlockPos topPos = reusablePos1.set(localX, actualYMax, localZ);
		BlockPos.MutableBlockPos bottomPos = reusablePos2.set(localX, actualYMax - 1, localZ);
		BlockState topState = blockView.getBlockState(topPos);

		while (bottomPos.getY() >= minY) {
			BlockState bottomState = blockView.getBlockState(bottomPos);
			if (faceBlocksLight(blockView, topState, bottomState, topPos, bottomPos)) {
				return transformOutputY(bottomPos.getY());
			}

			topState = bottomState;
			topPos.set(bottomPos);
			bottomPos.move(Direction.DOWN);
		}

		return -1;
	}

	@Override
	public int scanForWeepingSky(int localX, int yMax, int localZ) {
		BlockGetter blockView = checkBlockView();

		int actualYMax = transformInputY(yMax);

		BlockPos.MutableBlockPos topPos = reusablePos1.set(localX, actualYMax, localZ);
		BlockPos.MutableBlockPos bottomPos = reusablePos2.set(localX, actualYMax - 1, localZ);
		BlockState topState = blockView.getBlockState(topPos);

		if (CrystallineBlocks.isWeepingSky(topState))
			return yMax;

		while (bottomPos.getY() >= minY) {
			BlockState bottomState = blockView.getBlockState(bottomPos);
			if (CrystallineBlocks.isWeepingSky(bottomState)) {
				return transformOutputY(bottomPos.getY());
			}

			if (faceBlocksLight(blockView, topState, bottomState, topPos, bottomPos)) {
				return -1;
			}

			topState = bottomState;
			topPos.set(bottomPos);
			bottomPos.move(Direction.DOWN);
		}

		return -1;
	}

	@Override
	public boolean faceBlocksLight(int localX, int upperY, int localZ) {
		BlockGetter blockView = checkBlockView();

		int actualY = transformInputY(upperY);
		reusablePos1.set(localX, actualY, localZ);
		reusablePos2.set(localX, actualY - 1, localZ);
		BlockState upper = blockView.getBlockState(reusablePos1);
		BlockState lower = blockView.getBlockState(reusablePos2);

		return faceBlocksLight(blockView, upper, lower, reusablePos1, reusablePos2);
	}

	private static boolean faceBlocksLight(BlockGetter world, BlockState upper, BlockState lower, BlockPos upperPos, BlockPos lowerPos) {
		if (lower.getLightBlock(world, lowerPos) != 0) {
			return true;
		} else {
			VoxelShape voxelShape = LightEngine.getOcclusionShape(world, upperPos, upper, Direction.DOWN);
			VoxelShape voxelShape2 = LightEngine.getOcclusionShape(world, lowerPos, lower, Direction.UP);
			return Shapes.faceShapeOccludes(voxelShape, voxelShape2);
		}
	}
}
