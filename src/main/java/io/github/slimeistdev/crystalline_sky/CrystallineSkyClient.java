package io.github.slimeistdev.crystalline_sky;

import io.github.slimeistdev.crystalline_sky.extenders_cove.BlockRenderLayerExt;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;

public class CrystallineSkyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.putBlock(CrystallineBlocks.SKY, BlockRenderLayerExt.CRYSTALLINE_SKY_SKY);
	}
}
