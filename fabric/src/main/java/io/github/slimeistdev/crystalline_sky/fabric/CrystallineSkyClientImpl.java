package io.github.slimeistdev.crystalline_sky.fabric;

import io.github.slimeistdev.crystalline_sky.CrystallineSkyClient;
import io.github.slimeistdev.crystalline_sky.fabric.events.RegisterMaterialAtlasesEvent;
import io.github.slimeistdev.crystalline_sky.fabric.skybox_model.SkyboxModelLoadingPlugin;
import io.github.slimeistdev.crystalline_sky.multiloader.fabric.ItemGroupRegistrationEventImpl;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineAtlases;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class CrystallineSkyClientImpl implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CrystallineSkyClient.init();
		CrystallineSkyClient.registerKeybindings(KeyBindingHelper::registerKeyBinding);
		ItemGroupRegistrationEventImpl.register();

		Consumer<Block> makeSky = b -> BlockRenderLayerMap.INSTANCE.putBlock(b, CrystallineRenderTypes.SKY);
		Consumer<Block> makeSkybox = b -> BlockRenderLayerMap.INSTANCE.putBlock(b, CrystallineRenderTypes.SKYBOX);
		CrystallineBlocks.SKY.onRegistered(makeSky);
		CrystallineBlocks.WEEPING_SKY.onRegistered(makeSky);
		CrystallineBlocks.SKYBOX_TEST.onRegistered(makeSkybox);

		ClientTickEvents.END_CLIENT_TICK.register(CrystallineSkyClient::onEndTick);

		RegisterMaterialAtlasesEvent.EVENT.register(ctx -> CrystallineAtlases.register(ctx::addAtlas));
		SkyboxModelLoadingPlugin.register();
	}
}
