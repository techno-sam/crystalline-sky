package io.github.slimeistdev.crystalline_sky.items;

import io.github.slimeistdev.crystalline_sky.infrastructure.client.WeepingSkyDebugRenderer;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DebugRenderer_Duck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColumnPos;
import org.jetbrains.annotations.Nullable;

public class WeepingSkyBlockItemClient {
	// todo check if this is server-safe
	@Environment(EnvType.CLIENT)
	public static void setSelectedColumn(@Nullable ColumnPos column) {
		MinecraftClient mc = MinecraftClient.getInstance();
		WeepingSkyDebugRenderer weepingSkyDebugRenderer = ((DebugRenderer_Duck) mc.debugRenderer).crystalline_sky$getWeepingSkyDebugRenderer();
		weepingSkyDebugRenderer.setColumn(column);

		Text text = column == null
			? Text.translatable("message.crystalline_sky.weeping_sky_debug.column.cleared")
			: Text.translatable("message.crystalline_sky.weeping_sky_debug.column.set", column.x(), column.z());
		mc.inGameHud.setOverlayMessage(text, false);
	}
}
