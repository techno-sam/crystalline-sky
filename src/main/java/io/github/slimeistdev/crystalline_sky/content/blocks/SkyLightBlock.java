package io.github.slimeistdev.crystalline_sky.content.blocks;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.LightBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SkyLightBlock extends LightBlock {
	public SkyLightBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return context.isHolding(CrystallineItems.SKY_LIGHT) ? VoxelShapes.fullCube() : VoxelShapes.empty();
	}
}
