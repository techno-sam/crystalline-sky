package io.github.slimeistdev.crystalline_sky;

import io.github.slimeistdev.crystalline_sky.extenders_cove.BlockRenderLayerExt;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DebugRenderer_Duck;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class CrystallineSkyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.putBlock(CrystallineBlocks.SKY, BlockRenderLayerExt.CRYSTALLINE_SKY_SKY);
		BlockRenderLayerMap.putBlock(CrystallineBlocks.WEEPING_SKY, BlockRenderLayerExt.CRYSTALLINE_SKY_SKY);

		var weepingSkyKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"crystalline_sky.key.toggle_weeping_sky_debug",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"crystalline_sky.category.crystalline_sky"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (weepingSkyKeybind.wasPressed()) {
				int mode = ((DebugRenderer_Duck) client.debugRenderer).crystalline_sky$toggleWeepingSky();

				Text message = getDebugMessage(Formatting.YELLOW,
					Text.translatable("message.crystalline_sky.weeping_sky_debug." + mode)
				);

				client.inGameHud.getChatHud().addMessage(message);
			}
		});
	}

	@SuppressWarnings("SameParameterValue")
	private static Text getDebugMessage(Formatting formatting, Text message) {
		return Text.empty().append(Text.translatable("debug.prefix").formatted(formatting, Formatting.BOLD)).append(ScreenTexts.SPACE).append(message);
	}
}
