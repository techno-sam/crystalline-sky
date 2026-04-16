package io.github.slimeistdev.crystalline_sky;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.slimeistdev.crystalline_sky.annotation.multiloader.MultiLoaderEvent;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DebugRenderer_Duck;
import io.github.slimeistdev.crystalline_sky.multiloader.ClientPlatformHelper;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.LightBlock;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class CrystallineSkyClient {
	private static final KeyMapping KEY_TOGGLE_DEBUG = new KeyMapping(
		"crystalline_sky.key.toggle_weeping_sky_debug",
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_UNKNOWN,
		"crystalline_sky.category.crystalline_sky"
	);

	public static void init() {
		ClientPlatformHelper.registerItemProperty(
			CrystallineItems.SKY_LIGHT.value(),
			ResourceLocation.withDefaultNamespace("level"),
			(stack, world, entity, seed) -> {
				BlockItemStateProperties state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
				Integer level = state.get(LightBlock.LEVEL);
				return level != null ? level / 16.0F : 1.0F;
			}
		);
	}

	public static void registerKeybindings(Consumer<KeyMapping> registrar) {
		registrar.accept(KEY_TOGGLE_DEBUG);
	}

	@MultiLoaderEvent
	public static void onEndTick(Minecraft mc) {
		while (KEY_TOGGLE_DEBUG.consumeClick()) {
			int mode = ((DebugRenderer_Duck) mc.debugRenderer).crystalline_sky$toggleWeepingSky();

			Component message = getDebugMessage(ChatFormatting.YELLOW,
				Component.translatable("message.crystalline_sky.weeping_sky_debug." + mode)
			);

			mc.gui.getChat().addMessage(message);
		}
	}

	@SuppressWarnings("SameParameterValue")
	private static Component getDebugMessage(ChatFormatting formatting, Component message) {
		return Component.empty().append(Component.translatable("debug.prefix").withStyle(formatting, ChatFormatting.BOLD)).append(CommonComponents.SPACE).append(message);
	}
}
