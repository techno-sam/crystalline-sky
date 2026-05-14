package io.github.slimeistdev.crystalline_sky.neoforge;

import io.github.slimeistdev.crystalline_sky.CrystallineSky;
import io.github.slimeistdev.crystalline_sky.CrystallineSkyClient;
import io.github.slimeistdev.crystalline_sky.multiloader.neoforge.ItemGroupRegistrationEventImpl;
import io.github.slimeistdev.crystalline_sky.neoforge.skybox_model.SkyboxGeometryLoader;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineAtlases;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMaterialAtlasesEvent;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = CrystallineSky.ID)
public class CrystallineSkyClientImpl {
	public static void init() {
		CrystallineSkyClient.init();
	}

	@SubscribeEvent
	public static void registerNamedRenderTypes(RegisterNamedRenderTypesEvent event) {
		// NOTE: entity render type is just a placeholder, sky rendering isn't supported for entities
		event.register(CrystallineSky.id("sky"), CrystallineRenderTypes.SKY, RenderType.entitySolid(CrystallineSky.id("sky")));
		event.register(CrystallineSky.id("skybox"), CrystallineRenderTypes.SKYBOX, RenderType.entitySolid(CrystallineSky.id("skybox")));
	}

	@SubscribeEvent
	public static void registerBindings(RegisterKeyMappingsEvent event) {
		CrystallineSkyClient.registerKeybindings(event::register);
	}

	@SubscribeEvent
	public static void onEndTick(ClientTickEvent.Post event) {
		CrystallineSkyClient.onEndTick(Minecraft.getInstance());
	}

	@SubscribeEvent
	public static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
		CrystallineItems.onItemGroupRegistration(new ItemGroupRegistrationEventImpl(event));
	}

	@SubscribeEvent
	public static void registerAtlases(RegisterMaterialAtlasesEvent event) {
		CrystallineAtlases.register(event::register);
	}

	@SubscribeEvent
	public static void registerGeometryLoaders(RegisterGeometryLoaders event) {
		event.register(CrystallineSky.id("skybox"), new SkyboxGeometryLoader());
	}
}
