package io.github.slimeistdev.crystalline_sky.mixin;

import net.minecraft.world.level.lighting.SkyLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SkyLightSectionStorage.class)
public interface SkyLightSectionStorageAccessor extends LayerLightSectionStorageAccessor {
	@Invoker("hasLightDataAtOrBelow")
	boolean crystalline_sky$isAboveMinHeight(int sectionY);

	@Invoker("isAboveData")
	boolean crystalline_sky$isAtOrAboveTopmostSection(long sectionPos);

	@Invoker("getTopSectionY")
	int crystalline_sky$getTopSectionForColumn(long columnPos);

	@Invoker("getBottomSectionY")
	int crystalline_sky$getMinSectionY();
}
