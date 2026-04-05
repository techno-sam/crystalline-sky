package io.github.slimeistdev.crystalline_sky.content.items;

import io.github.slimeistdev.crystalline_sky.registry.CrystallineDataComponentTypes;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.Level;

public class WeepingSkyBlockItem extends BlockItem {
	public WeepingSkyBlockItem(Block block, Properties settings) {
		super(block, settings);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		if (stack.get(CrystallineDataComponentTypes.WEEPING_SKY_DEBUG_COLUMN_TOOL) != null) {
			if (context.getLevel().isClientSide) {
				BlockPos pos = context.getClickedPos();
				WeepingSkyBlockItemClient.setSelectedColumn(new ColumnPos(pos.getX(), pos.getZ()));
			}
			return InteractionResult.SUCCESS;
		}

		return super.useOn(context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand);
		if (stack.get(CrystallineDataComponentTypes.WEEPING_SKY_DEBUG_COLUMN_TOOL) != null) {
			if (world.isClientSide) {
				WeepingSkyBlockItemClient.setSelectedColumn(null);
			}
			return InteractionResultHolder.success(stack);
		}

		return super.use(world, user, hand);
	}
}
