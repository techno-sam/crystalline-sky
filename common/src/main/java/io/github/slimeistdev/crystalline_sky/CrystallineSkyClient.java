package io.github.slimeistdev.crystalline_sky;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DebugRenderer_Duck;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.LightBlock;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class CrystallineSkyClient {
	public static void init() {
		BlockRenderLayerMap.INSTANCE.putBlock(CrystallineBlocks.SKY, CrystallineRenderTypes.SKY);
		BlockRenderLayerMap.INSTANCE.putBlock(CrystallineBlocks.WEEPING_SKY, CrystallineRenderTypes.SKY);

		var weepingSkyKeybind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"crystalline_sky.key.toggle_weeping_sky_debug",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"crystalline_sky.category.crystalline_sky"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (weepingSkyKeybind.consumeClick()) {
				int mode = ((DebugRenderer_Duck) client.debugRenderer).crystalline_sky$toggleWeepingSky();

				Component message = getDebugMessage(ChatFormatting.YELLOW,
					Component.translatable("message.crystalline_sky.weeping_sky_debug." + mode)
				);

				client.gui.getChat().addMessage(message);
			}
		});

		ItemProperties.register(CrystallineItems.SKY_LIGHT, ResourceLocation.withDefaultNamespace("level"),
			(stack, world, entity, seed) -> {
				BlockItemStateProperties blockStateComponent = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
				Integer integer = blockStateComponent.get(LightBlock.LEVEL);
				return integer != null ? integer / 16.0F : 1.0F;
			});
	}

	@SuppressWarnings("SameParameterValue")
	private static Component getDebugMessage(ChatFormatting formatting, Component message) {
		return Component.empty().append(Component.translatable("debug.prefix").withStyle(formatting, ChatFormatting.BOLD)).append(CommonComponents.SPACE).append(message);
	}
}
