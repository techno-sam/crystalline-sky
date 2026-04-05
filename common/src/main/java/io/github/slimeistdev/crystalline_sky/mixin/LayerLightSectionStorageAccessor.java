package io.github.slimeistdev.crystalline_sky.mixin;

import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LayerLightSectionStorage.class)
public interface LayerLightSectionStorageAccessor {
	@Invoker("lightOnInSection")
	boolean crystalline_sky$callLightOnInSection(long sectionPos);

	@Invoker("storingLightForSection")
	boolean crystalline_sky$hasSection(long sectionPos);

	@Invoker("getStoredLevel")
	int crystalline_sky$get(long blockPos);

	@Invoker("setStoredLevel")
	void crystalline_sky$set(long blockPos, int lightLevel);

	@Invoker("getDataLayerToWrite")
	@Nullable DataLayer crystalline_sky$method_51547(long sectionPos);
}
