package io.github.slimeistdev.crystalline_sky.content.items;

import io.github.slimeistdev.crystalline_sky.infrastructure.client.WeepingSkyDebugRenderer;
import io.github.slimeistdev.crystalline_sky.mixin_ducks.client.DebugRenderer_Duck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ColumnPos;
import org.jetbrains.annotations.Nullable;

public class WeepingSkyBlockItemClient {
	// todone check if this is server-safe
	@Environment(EnvType.CLIENT)
	public static void setSelectedColumn(@Nullable ColumnPos column) {
		Minecraft mc = Minecraft.getInstance();
		WeepingSkyDebugRenderer weepingSkyDebugRenderer = ((DebugRenderer_Duck) mc.debugRenderer).crystalline_sky$getWeepingSkyDebugRenderer();
		weepingSkyDebugRenderer.setColumn(column);

		Component text = column == null
			? Component.translatable("message.crystalline_sky.weeping_sky_debug.column.cleared")
			: Component.translatable("message.crystalline_sky.weeping_sky_debug.column.set", column.x(), column.z());
		mc.gui.setOverlayMessage(text, false);
	}
}
