package io.github.slimeistdev.crystalline_sky.infrastructure;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MutableWeepingScanner implements WeepingStorage.WeepingScanner {
	private @Nullable BlockView blockView;
	private final int minY;

	private final BlockPos.Mutable reusablePos1 = new BlockPos.Mutable();
	private final BlockPos.Mutable reusablePos2 = new BlockPos.Mutable();

	public MutableWeepingScanner(int minY) {
		this.minY = minY;
	}

	public void setBlockView(@Nullable BlockView blockView) {
		this.blockView = blockView;
	}

	private int transformInputY(int y) {
		return y + minY;
	}

	private int transformOutputY(int y) {
		return y == Integer.MIN_VALUE ? -1 : y - minY;
	}

	private @NotNull BlockView checkBlockView() {
		if (blockView == null) {
			throw new IllegalStateException("blockView is not set");
		} else {
			return blockView;
		}
	}

	@Override
	public int scan(int localX, int yMax, int localZ) {
		BlockView blockView = checkBlockView();

		int actualYMax = transformInputY(yMax);

		BlockPos.Mutable topPos = reusablePos1.set(localX, actualYMax, localZ);
		BlockPos.Mutable bottomPos = reusablePos2.set(localX, actualYMax - 1, localZ);
		BlockState topState = blockView.getBlockState(topPos);

		while (bottomPos.getY() >= minY) {
			BlockState bottomState = blockView.getBlockState(bottomPos);
			if (faceBlocksLight(topState, bottomState)) {
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
		BlockView blockView = checkBlockView();

		int actualYMax = transformInputY(yMax);

		BlockPos.Mutable topPos = reusablePos1.set(localX, actualYMax, localZ);
		BlockPos.Mutable bottomPos = reusablePos2.set(localX, actualYMax - 1, localZ);
		BlockState topState = blockView.getBlockState(topPos);

		if (CrystallineBlocks.isWeepingSky(topState))
			return yMax;

		while (bottomPos.getY() >= minY) {
			BlockState bottomState = blockView.getBlockState(bottomPos);
			if (CrystallineBlocks.isWeepingSky(bottomState)) {
				return transformOutputY(bottomPos.getY());
			}

			if (faceBlocksLight(topState, bottomState)) {
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
		BlockView blockView = checkBlockView();

		int actualY = transformInputY(upperY);
		reusablePos1.set(localX, actualY, localZ);
		reusablePos2.set(localX, actualY - 1, localZ);
		BlockState upper = blockView.getBlockState(reusablePos1);
		BlockState lower = blockView.getBlockState(reusablePos2);

		return faceBlocksLight(upper, lower);
	}

	private static boolean faceBlocksLight(BlockState upper, BlockState lower) {
		if (lower.getOpacity() != 0) {
			return true;
		} else {
			VoxelShape voxelShape = ChunkLightProvider.getOpaqueShape(upper, Direction.DOWN);
			VoxelShape voxelShape2 = ChunkLightProvider.getOpaqueShape(lower, Direction.UP);
			return VoxelShapes.unionCoversFullCube(voxelShape, voxelShape2);
		}
	}
}
