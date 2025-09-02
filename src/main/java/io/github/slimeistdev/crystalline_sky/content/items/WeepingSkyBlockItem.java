package io.github.slimeistdev.crystalline_sky.content.items;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineDataComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.World;

public class WeepingSkyBlockItem extends BlockItem {
	public WeepingSkyBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		ItemStack stack = context.getStack();
		if (stack.get(CrystallineDataComponentTypes.WEEPING_SKY_DEBUG_COLUMN_TOOL) != null) {
			if (context.getWorld().isClient) {
				BlockPos pos = context.getBlockPos();
				WeepingSkyBlockItemClient.setSelectedColumn(new ColumnPos(pos.getX(), pos.getZ()));
			}
			return ActionResult.SUCCESS;
		}

		return super.useOnBlock(context);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (stack.get(CrystallineDataComponentTypes.WEEPING_SKY_DEBUG_COLUMN_TOOL) != null) {
			if (world.isClient) {
				WeepingSkyBlockItemClient.setSelectedColumn(null);
			}
			return ActionResult.SUCCESS;
		}

		return super.use(world, user, hand);
	}
}
