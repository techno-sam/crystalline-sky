package io.github.slimeistdev.crystalline_sky.mixin;

import net.minecraft.world.chunk.light.SkyLightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SkyLightStorage.class)
public interface SkyLightStorageAccessor extends LightStorageAccessor {
	@Invoker("isAboveMinHeight")
	boolean crystalline_sky$isAboveMinHeight(int sectionY);

	@Invoker("isAtOrAboveTopmostSection")
	boolean crystalline_sky$isAtOrAboveTopmostSection(long sectionPos);

	@Invoker("getTopSectionForColumn")
	int crystalline_sky$getTopSectionForColumn(long columnPos);

	@Invoker("getMinSectionY")
	int crystalline_sky$getMinSectionY();
}
