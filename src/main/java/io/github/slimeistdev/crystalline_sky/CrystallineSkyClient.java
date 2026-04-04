package io.github.slimeistdev.crystalline_sky;

import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DebugRenderer_Duck;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineBlocks;
import io.github.slimeistdev.crystalline_sky.registry.CrystallineItems;
import io.github.slimeistdev.crystalline_sky.registry.client.CrystallineRenderLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.LightBlock;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class CrystallineSkyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(CrystallineBlocks.SKY, CrystallineRenderLayers.SKY);
		BlockRenderLayerMap.INSTANCE.putBlock(CrystallineBlocks.WEEPING_SKY, CrystallineRenderLayers.SKY);

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

		ModelPredicateProviderRegistry.register(CrystallineItems.SKY_LIGHT, Identifier.ofVanilla("level"),
			(stack, world, entity, seed) -> {
				BlockStateComponent blockStateComponent = stack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
				Integer integer = blockStateComponent.getValue(LightBlock.LEVEL_15);
				return integer != null ? integer / 16.0F : 1.0F;
			});
	}

	@SuppressWarnings("SameParameterValue")
	private static Text getDebugMessage(Formatting formatting, Text message) {
		return Text.empty().append(Text.translatable("debug.prefix").formatted(formatting, Formatting.BOLD)).append(ScreenTexts.SPACE).append(message);
	}
}
