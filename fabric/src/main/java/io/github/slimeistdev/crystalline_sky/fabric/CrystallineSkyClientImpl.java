package io.github.slimeistdev.crystalline_sky.fabric;

import io.github.slimeistdev.crystalline_sky.CrystallineSkyClient;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

@Environment(EnvType.CLIENT)
public class CrystallineSkyClientImpl implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CrystallineSkyClient.init();
		CrystallineSkyClient.registerKeybindings(KeyBindingHelper::registerKeyBinding);

		BlockRenderLayerMap.INSTANCE.putBlock(CrystallineBlocks.SKY, CrystallineRenderTypes.SKY);
		BlockRenderLayerMap.INSTANCE.putBlock(CrystallineBlocks.WEEPING_SKY, CrystallineRenderTypes.SKY);

		ClientTickEvents.END_CLIENT_TICK.register(CrystallineSkyClient::onEndTick);
	}
}
