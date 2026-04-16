package io.github.slimeistdev.crystalline_sky.foundation.registration.holder;

import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockHolder<T extends Block> extends ItemLikeHolder<T> {
	public BlockHolder(HolderOwner<T> owner, ResourceKey<T> key) {
		super(owner, key);
	}

	public BlockState defaultBlockState() {
		return value().defaultBlockState();
	}

	public boolean is(BlockState state) {
		return is(state.getBlock());
	}
}
