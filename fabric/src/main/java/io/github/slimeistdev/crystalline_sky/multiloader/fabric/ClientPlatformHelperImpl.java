package io.github.slimeistdev.crystalline_sky.multiloader.fabric;

import io.github.slimeistdev.crystalline_sky.annotation.multiloader.ImplClass;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

@ImplClass
public class ClientPlatformHelperImpl {
	public static void registerItemProperty(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
		ItemProperties.register(item, name, property);
	}
}
