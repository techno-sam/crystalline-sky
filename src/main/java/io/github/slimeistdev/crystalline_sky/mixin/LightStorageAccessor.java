package io.github.slimeistdev.crystalline_sky.mixin;

import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LightStorage.class)
public interface LightStorageAccessor {
	@Invoker("isSectionInEnabledColumn")
	boolean crystalline_sky$callIsSectionInEnabledColumn(long sectionPos);
}
