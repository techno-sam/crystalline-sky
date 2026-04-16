package io.github.slimeistdev.crystalline_sky.content.blocks;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkyLightBlock extends LightBlock {
	public SkyLightBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return context.isHoldingItem(CrystallineItems.SKY_LIGHT.value()) ? Shapes.block() : Shapes.empty();
	}
}
