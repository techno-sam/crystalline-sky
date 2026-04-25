package io.github.slimeistdev.crystalline_sky.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public interface RegisterMaterialAtlasesEvent {
	Event<RegisterMaterialAtlasesEvent> EVENT = EventFactory.createArrayBacked(RegisterMaterialAtlasesEvent.class, (callbacks) -> (context) -> {
		for (RegisterMaterialAtlasesEvent callback : callbacks) {
			callback.run(context);
		}
	});

	@ApiStatus.Internal
	static Map<ResourceLocation, ResourceLocation> gatherMaterialAtlases(Map<ResourceLocation, ResourceLocation> vanillaAtlases) {
		Map<ResourceLocation, ResourceLocation> atlases = new HashMap<>(vanillaAtlases);
		EVENT.invoker().run(((atlasLocation, atlasInfoLocation) -> {
			ResourceLocation oldAtlasInfoLoc = atlases.putIfAbsent(atlasLocation, atlasInfoLocation);
			if (oldAtlasInfoLoc != null) {
				throw new IllegalStateException(String.format(
					"Duplicate registration of atlas: %s (old info: %s, new info: %s)",
					atlasLocation,
					oldAtlasInfoLoc,
					atlasInfoLocation));
			}
		}));
		return Map.copyOf(atlases);
	}

	void run(Context context);

	@FunctionalInterface
	interface Context {
		void addAtlas(ResourceLocation atlasLocation, ResourceLocation atlasInfoLocation);
	}
}
