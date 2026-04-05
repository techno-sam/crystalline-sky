package io.github.slimeistdev.crystalline_sky.multiloader;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

@Environment(EnvType.CLIENT)
public class ClientPlatformHelper {
	@ExpectPlatform
	public static void registerItemProperty(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
		throw new AssertionError();
	}
}
